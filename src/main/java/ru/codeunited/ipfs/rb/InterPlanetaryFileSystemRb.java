package ru.codeunited.ipfs.rb;

import com.netflix.ribbon.ClientOptions;
import com.netflix.ribbon.Ribbon;
import com.netflix.ribbon.RibbonRequest;
import com.netflix.ribbon.http.HttpRequestTemplate;
import com.netflix.ribbon.http.HttpResourceGroup;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.codeunited.ipfs.Block;
import ru.codeunited.ipfs.Dht;
import ru.codeunited.ipfs.IPFS;
import ru.codeunited.ipfs.Swarm;
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
public class InterPlanetaryFileSystemRb implements IPFS {

    private final Logger log = LoggerFactory.getLogger(InterPlanetaryFileSystemRb.class);

    private final HttpResourceGroup httpResourceGroup;

    private final HttpRequestTemplate.Builder<ByteBuf> templateBuilder;

    private final SwarmRb swarm;

    private static final int BUFFER_FRAME = 32768;

    private Supplier<String> boundarySupplier = () -> {
        Random r = new Random();
        String allowed = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < 32; i++)
            b.append(allowed.charAt(r.nextInt(allowed.length())));
        return b.toString();
    };

    private final HttpRequestTemplate<ByteBuf>
            rootVersion,
            rootCommands,
            rootCat,
            rootRefs,
            rootRefsLocal,
            rootAdd;

    InterPlanetaryFileSystemRb(ClientOptions options) {
        httpResourceGroup = Ribbon.createHttpResourceGroup("ipfs", options);
        templateBuilder = httpResourceGroup.newTemplateBuilder("root", ByteBuf.class);

        // roots methods
        rootVersion = templateBuilder.withMethod("GET").withUriTemplate("/api/v0/version").build();
        rootCommands = templateBuilder.withMethod("GET").withUriTemplate("/api/v0/commands").build();
        rootCat = templateBuilder.withMethod("GET").withUriTemplate("/api/v0/cat?arg={multihash}").build();
        rootAdd = templateBuilder.withMethod("POST").withUriTemplate("/api/v0/add").build();

        // refs methods
        rootRefs = templateBuilder.withMethod("GET").withUriTemplate("/api/v0/refs?arg={multihash}").build();
        rootRefsLocal = templateBuilder.withMethod("GET").withUriTemplate("/api/v0/refs/local").build();

        swarm = new SwarmRb(httpResourceGroup);
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
        return rootCat.requestBuilder().withRequestProperty("multihash", multihash).build();
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
    public RibbonRequest<ByteBuf> get(String multihash) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public RibbonRequest<ByteBuf> refs(String multihash) {
        return rootRefs.requestBuilder().withRequestProperty("multihash", multihash).build();
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
    public Dht dht() {
        return null;
    }
}
