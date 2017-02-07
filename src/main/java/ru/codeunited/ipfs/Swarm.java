package ru.codeunited.ipfs;

import com.netflix.ribbon.RibbonRequest;
import io.netty.buffer.ByteBuf;

/**
 * Created by ikonovalov on 02/02/17.
 */
public interface Swarm {

    RibbonRequest<ByteBuf> peers();

}
