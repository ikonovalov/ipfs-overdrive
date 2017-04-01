package ru.codeunited.ipfs;

import com.netflix.ribbon.RibbonRequest;
import io.ipfs.multihash.Multihash;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;

public interface IPFS {

    RibbonRequest<ByteBuf> id();

    RibbonRequest<ByteBuf> version();

    RibbonRequest<ByteBuf> commands();

    RibbonRequest<ByteBuf> cat(String multihash);

    RibbonRequest<ByteBuf> add(InputStream stream) throws IOException;

    RibbonRequest<ByteBuf> catSingle(String multihash);

    RibbonRequest<ByteBuf> get(String multihash);

    RibbonRequest<ByteBuf> refs(String multihash);

    RibbonRequest<ByteBuf> refsLocal();

    Swarm swarm();

    Block block();

    Dht dht();

    default RibbonRequest<ByteBuf> get(Multihash multihash) {
        return get(multihash.toBase58());
    }

    default RibbonRequest<ByteBuf> cat(Multihash multihash) {
        return cat(multihash.toBase58());
    }

    default RibbonRequest<ByteBuf> catSingle(Multihash multihash) {
        return catSingle(multihash.toBase58());
    }

    default RibbonRequest<ByteBuf> refs(Multihash multihash) {
        return refs(multihash.toBase58());
    }
}

