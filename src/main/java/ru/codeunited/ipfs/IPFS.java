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

import com.netflix.ribbon.RibbonRequest;
import io.ipfs.multihash.Multihash;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public interface IPFS {

    // Basic commands
    RibbonRequest<ByteBuf> add(InputStream stream) throws IOException;

    RibbonRequest<ByteBuf> cat(String multihash);

    RibbonRequest<ByteBuf> catSingle(String multihash);

    RibbonRequest<ByteBuf> get(String multihash);

    RibbonRequest<ByteBuf> ls(String multihash);

    RibbonRequest<ByteBuf> refs(String multihash);

    RibbonRequest<ByteBuf> refsLocal();


    // Data structure commands
    Block block();

    Objects objects();

    Files files();

    Dag dag();


    // Advanced commands


    // Network commands
    RibbonRequest<ByteBuf> id();

    Bootstrap bootstrap();

    Swarm swarm();

    Dht dht();

    Ping ping();


    // Tool commands
    Config config();

    RibbonRequest<ByteBuf> version();

    RibbonRequest<ByteBuf> commands();


    // Multihash analogs
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

