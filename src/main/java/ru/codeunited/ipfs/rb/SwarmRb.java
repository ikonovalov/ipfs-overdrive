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

import com.netflix.ribbon.RibbonRequest;
import com.netflix.ribbon.http.HttpRequestTemplate;
import com.netflix.ribbon.http.HttpResourceGroup;
import io.netty.buffer.ByteBuf;
import ru.codeunited.ipfs.Swarm;

/**
 * Created by ikonovalov on 03/02/17.
 */
public class SwarmRb implements Swarm {

    private final HttpRequestTemplate<ByteBuf> peers;

    SwarmRb(HttpResourceGroup httpResourceGroup) {

        peers = httpResourceGroup.newTemplateBuilder("ipfs_swarm_peers", ByteBuf.class)
                .withMethod("GET")
                .withUriTemplate("/api/v0/swarm/peers")
                .build();
    }

    @Override
    public RibbonRequest<ByteBuf> peers() {
        return peers.requestBuilder().build();
    }
}
