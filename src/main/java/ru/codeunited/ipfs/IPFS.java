package ru.codeunited.ipfs;

import com.netflix.ribbon.RibbonRequest;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;

public interface IPFS {

    RibbonRequest<ByteBuf> version();

    RibbonRequest<ByteBuf> commands();

    RibbonRequest<ByteBuf> cat(String multihash);

    RibbonRequest<ByteBuf> add(InputStream stream) throws IOException;

    RibbonRequest<ByteBuf> get(String multihash);

    RibbonRequest<ByteBuf> refs(String multihash);

    RibbonRequest<ByteBuf> refsLocal();

}

