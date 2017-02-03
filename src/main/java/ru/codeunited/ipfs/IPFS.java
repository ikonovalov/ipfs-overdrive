package ru.codeunited.ipfs;

import com.netflix.ribbon.RibbonRequest;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;

public interface IPFS {

    RibbonRequest<ByteBuf> version();

    RibbonRequest<ByteBuf> commands();

    RibbonRequest<ByteBuf> cat(String multihash);

    RibbonRequest<ByteBuf> add(InputStream stream);

}

