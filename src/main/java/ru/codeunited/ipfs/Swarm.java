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
import io.ipfs.multiaddr.MultiAddress;
import io.ipfs.multihash.Multihash;
import io.netty.buffer.ByteBuf;

/**
 * Created by ikonovalov on 02/02/17.
 */
public interface Swarm {

    RibbonRequest<ByteBuf> peers();

    RibbonRequest<ByteBuf> addrs();

    RibbonRequest<ByteBuf> connect(String address);

    RibbonRequest<ByteBuf> disconnect(String address);

    default RibbonRequest<ByteBuf> connect(MultiAddress address) {
        return connect(address.toString());
    }

    default RibbonRequest<ByteBuf> disconnect(MultiAddress address) {
        return disconnect(address.toString());
    }

}
