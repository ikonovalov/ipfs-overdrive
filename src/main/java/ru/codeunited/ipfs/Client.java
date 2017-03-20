package ru.codeunited.ipfs;

import io.ipfs.multiaddr.MultiAddress;
import io.ipfs.multihash.Multihash;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * OSS codeunited.ru
 * Created by ikonovalov on 06/03/17.
 */
public class Client implements JsonSupports {

    private final IPFS ipfs;

    public Client(IPFS ipfs) {
        this.ipfs = ipfs;
    }

    public List<Peer> getPeers() {
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
