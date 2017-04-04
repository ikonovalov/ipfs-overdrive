/*
 *   Copyright (C) 2017 Igor Konovalov
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package ru.codeunited.ipfs.rb;

import com.netflix.ribbon.ClientOptions;
import com.netflix.ribbon.RibbonRequest;
import com.netflix.ribbon.RibbonResourceFactory;
import com.netflix.ribbon.http.HttpRequestTemplate;
import com.netflix.ribbon.http.HttpResourceGroup;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.codeunited.ipfs.*;
import rx.Observable;
import rx.observables.StringObservable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.function.Supplier;

/**
 * OSS codeunited.ru
 * Created by ikonovalov on 03/02/17.
 */
public class IPFSRb implements IPFS {

    private final Logger log = LoggerFactory.getLogger(IPFSRb.class);

    private final SwarmRb swarm;

    private static final int BUFFER_FRAME = 1024 * 256;

    private Supplier<String> boundarySupplier = () -> {
        Random r = new Random();
        String allowed = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < 32; i++)
            b.append(allowed.charAt(r.nextInt(allowed.length())));
        return b.toString();
    };

    // Available HTTP request remplates
    private final HttpRequestTemplate<ByteBuf>
            rootId,
            rootVersion,
            rootCommands,
            rootCat,
            rootCatAggregated,
            rootGet,
            rootRefs,
            rootRefsLocal,
            rootAdd,
            rootLs;

    IPFSRb(ClientOptions options) {
        RibbonResourceFactory ribbonResourceFactory = IPFSRibbonResourceFactory.normalResourceFactory();
        RibbonResourceFactory oversizeResourceFactory = IPFSRibbonResourceFactory.oversizeReourceFactory();

        HttpResourceGroup httpResourceGroup = ribbonResourceFactory.createHttpResourceGroup("ipfs", options);
        HttpResourceGroup oversizeHttpResourceGroup = oversizeResourceFactory.createHttpResourceGroup("ipfs_oversize", options);

        // roots methods
        rootId = httpResourceGroup
                .newTemplateBuilder("ipfs_id", ByteBuf.class)
                .withMethod("GET")
                .withUriTemplate("/api/v0/id")
                .build();

        rootVersion = httpResourceGroup
                .newTemplateBuilder("ipfs_version", ByteBuf.class)
                .withMethod("GET")
                .withUriTemplate("/api/v0/version")
                .build();

        rootCommands = httpResourceGroup
                .newTemplateBuilder("ipfs_commands", ByteBuf.class)
                .withMethod("GET")
                .withUriTemplate("/api/v0/commands")
                .build();

        rootCat = oversizeHttpResourceGroup
                .newTemplateBuilder("ipfs_cat", ByteBuf.class)
                .withMethod("GET")
                .withUriTemplate("/api/v0/cat?arg={multihash}")
                .build();

        rootCatAggregated = httpResourceGroup
                .newTemplateBuilder("ipfs_cat", ByteBuf.class)
                .withMethod("GET")
                .withUriTemplate("/api/v0/cat?arg={multihash}")
                .build();

        rootGet = oversizeHttpResourceGroup
                .newTemplateBuilder("ipfs_get", ByteBuf.class)
                .withMethod("GET")
                .withUriTemplate("/api/v0/get?arg={multihash}")
                .build();

        rootAdd = httpResourceGroup
                .newTemplateBuilder("ipfs_add", ByteBuf.class)
                .withMethod("POST")
                .withUriTemplate("/api/v0/add")
                .build();

        rootLs = oversizeHttpResourceGroup
                .newTemplateBuilder("ipfs_ls", ByteBuf.class)
                .withMethod("GET")
                .withUriTemplate("/api/v0/ls?arg={multihash}")
                .build();


        // refs methods
        rootRefs = httpResourceGroup
                .newTemplateBuilder("ipfs_refs", ByteBuf.class)
                .withMethod("GET")
                .withUriTemplate("/api/v0/refs?arg={multihash}")
                .build();

        rootRefsLocal = httpResourceGroup
                .newTemplateBuilder("ipfs_local", ByteBuf.class)
                .withMethod("GET")
                .withUriTemplate("/api/v0/refs/local")
                .build();

        swarm = new SwarmRb(httpResourceGroup);
    }

    @Override
    public RibbonRequest<ByteBuf> id() {
        return rootId.requestBuilder().build();
    }

    @Override
    public Bootstrap bootstrap() {
        return null;
    }

    @Override
    public RibbonRequest<ByteBuf> version() {
        return rootVersion.requestBuilder().build();
    }

    @Override
    public RibbonRequest<ByteBuf> commands() {
        return rootCommands.requestBuilder().build();
    }

    @Override
    public RibbonRequest<ByteBuf> cat(String multihash) {
        return rootCat.requestBuilder()
                .withRequestProperty("multihash", multihash)
                .build();
    }

    @Override
    public RibbonRequest<ByteBuf> catSingle(String multihash) {
        return rootCatAggregated.requestBuilder()
                .withRequestProperty("multihash", multihash)
                .build();
    }

    @Override
    public RibbonRequest<ByteBuf> get(String multihash) {
        return rootGet.requestBuilder()
                .withRequestProperty("multihash", multihash)
                .build();
    }

    @Override
    public RibbonRequest<ByteBuf> ls(String multihash) {
        return rootLs.requestBuilder()
                .withRequestProperty("multihash", multihash)
                .build();
    }

    @Override
    public RibbonRequest<ByteBuf> add(InputStream stream) throws IOException {
        final String boundary = boundarySupplier.get();
        return rootAdd
                .requestBuilder()
                .withHeader("Content-Type", "multipart/form-data; boundary=" + boundary)
                .withContent(
                        Observable.concat(
                                Observable.just(Unpooled.buffer(128)
                                        .writeBytes(("--" + boundary + "\r\n").getBytes())
                                        .writeBytes("Content-Disposition: file;\r\n".getBytes())
                                        .writeBytes("Content-Type: application/octet-stream\r\n".getBytes())
                                        .writeBytes("Content-Transfer-Encoding: binary\r\n\r\n".getBytes())),

                                StringObservable.from(stream, BUFFER_FRAME)
                                        .map(buffer -> Unpooled.buffer(BUFFER_FRAME).writeBytes(buffer))
                                        .doOnError(throwable -> log.error("Data chunk handling error", throwable))
                                        .doOnCompleted(() -> log.debug("Incoming stream exhausted")),

                                Observable.just(Unpooled.buffer(64).writeBytes(("\r\n--" + boundary + "--\r\n").getBytes()))
                        )
                ).build();
    }

    @Override
    public RibbonRequest<ByteBuf> refs(String multihash) {
        return rootRefs.requestBuilder()
                .withRequestProperty("multihash", multihash)
                .build();
    }

    @Override
    public RibbonRequest<ByteBuf> refsLocal() {
        return rootRefsLocal.requestBuilder().build();
    }

    @Override
    public Swarm swarm() {
        return swarm;
    }

    @Override
    public Block block() {
        return null;
    }

    @Override
    public Objects objects() {
        return null;
    }

    @Override
    public Files files() {
        return null;
    }

    @Override
    public Dag dag() {
        return null;
    }

    @Override
    public Dht dht() {
        return null;
    }

    @Override
    public Ping ping() {
        return null;
    }

    @Override
    public Config config() {
        return null;
    }
}
