package ru.codeunited.ipfs;

import com.netflix.ribbon.RibbonRequest;
import com.netflix.ribbon.proxy.annotation.Http;
import io.netty.buffer.ByteBuf;

import static com.netflix.ribbon.proxy.annotation.Http.HttpMethod.GET;

/**
 * Created by ikonovalov on 02/02/17.
 */
public interface Swarm {

    RibbonRequest<ByteBuf> swarmPeers();

}
