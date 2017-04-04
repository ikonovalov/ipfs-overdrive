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

import io.ipfs.multiaddr.MultiAddress;
import io.ipfs.multihash.Multihash;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

/**
 * OSS codeunited.ru
 * Created by ikonovalov on 06/03/17.
 */
public class Client implements JsonSupports {

    private final IPFS ipfs;

    public Client(IPFS ipfs) {
        this.ipfs = ipfs;
    }

    /**
     * Receive IPFS node ID.
     * @return
     */
    public NodeId id() {
        Map<String, Object> response = json(ipfs.id().execute());
        return new NodeId(
                Multihash.fromBase58((String) response.get("ID")),
                Base64.getDecoder().decode((String) response.get("PublicKey")),
                ((List<String>)response.get("Addresses")).stream().map(MultiAddress::new).collect(toSet()),
                (String) response.get("AgentVersion"),
                (String) response.get("ProtocolVersion")
        );
    }

    /**
     * Lists all connected peers.
     * @return
     */
    public List<Peer> peers() {
        Map<String, Object> response = json(ipfs.swarm().peers().execute());
        List<Map<String, String>> peers = (List<Map<String, String>>) response.get("Peers");
        return peers.stream().map(
                jpm -> new Peer(
                        new MultiAddress(jpm.get("Addr")),
                        Multihash.fromBase58(jpm.get("Peer")),
                        Optional.ofNullable(jpm.get("Latency")).orElse("0"),
                        jpm.get("Muxer"),
                        jpm.get("Streams")
                )
        ).collect(Collectors.toList());
    }

}
