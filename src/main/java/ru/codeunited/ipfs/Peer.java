package ru.codeunited.ipfs;

import com.google.common.base.MoreObjects;
import io.ipfs.multiaddr.MultiAddress;
import io.ipfs.multihash.Multihash;

/**
 * OSS codeunited.ru
 * Created by ikonovalov on 20/03/17.
 */
public final class Peer {

    private final MultiAddress address;

    private final Multihash hash;

    private final String latency;

    private final String muxer;

    private final String streams;

    public Peer(MultiAddress address, Multihash hash, String latency, String muxer, String streams) {
        this.address = address;
        this.hash = hash;
        this.latency = latency;
        this.muxer = muxer;
        this.streams = streams;
    }

    public MultiAddress getAddress() {
        return address;
    }

    public Multihash getHash() {
        return hash;
    }

    public String getLatency() {
        return latency;
    }

    public String getMuxer() {
        return muxer;
    }

    public String getStreams() {
        return streams;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("address", address)
                .add("hash", hash)
                .add("latency", latency)
                .add("muxer", muxer)
                .add("streams", streams)
                .toString();
    }
}
