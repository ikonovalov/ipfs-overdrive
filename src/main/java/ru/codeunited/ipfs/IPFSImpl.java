package ru.codeunited.ipfs;

import com.netflix.ribbon.ClientOptions;
import com.netflix.ribbon.Ribbon;
import com.netflix.ribbon.RibbonRequest;
import com.netflix.ribbon.http.HttpRequestTemplate;
import com.netflix.ribbon.http.HttpResourceGroup;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;

/**
 * OSS codeunited.ru
 * Created by ikonovalov on 03/02/17.
 */
public class IPFSImpl implements IPFS {

    private final HttpResourceGroup httpResourceGroup;

    private final HttpRequestTemplate.Builder<ByteBuf> templateBuilder;

    private final HttpRequestTemplate<ByteBuf> rootVersion;
    private final HttpRequestTemplate<ByteBuf> rootCommands;
    private final HttpRequestTemplate<ByteBuf> rootCat;

    IPFSImpl(ClientOptions options) {
        httpResourceGroup = Ribbon.createHttpResourceGroup("ipfs", options);
        templateBuilder = httpResourceGroup.newTemplateBuilder("root", ByteBuf.class);

        rootVersion = templateBuilder.withMethod("GET").withUriTemplate("/api/v0/version").build();
        rootCommands = templateBuilder.withMethod("GET").withUriTemplate("/api/v0/commands").build();
        rootCat = templateBuilder.withMethod("GET").withUriTemplate("/api/v0/cat?arg={multihash}").build();
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
    public RibbonRequest<ByteBuf> add(InputStream stream) {
        throw new IllegalStateException("Not implemented");
    }
}
