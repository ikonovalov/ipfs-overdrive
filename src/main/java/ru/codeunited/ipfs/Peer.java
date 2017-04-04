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
