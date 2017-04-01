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
