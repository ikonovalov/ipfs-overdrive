package ru.codeunited.ipfs;

import com.netflix.ribbon.RibbonRequest;
import com.netflix.ribbon.proxy.annotation.ClientProperties;
import com.netflix.ribbon.proxy.annotation.ClientProperties.Property;
import com.netflix.ribbon.proxy.annotation.Http;
import com.netflix.ribbon.proxy.annotation.Var;
import io.netty.buffer.ByteBuf;

import static com.netflix.ribbon.proxy.annotation.Http.HttpMethod.GET;

/**
 * Created by ikonovalov on 01/02/17.
 */
@ClientProperties(
        properties = {
                @Property(name = "ReadTimeout", value = "2000"),
                @Property(name = "ConnectTimeout", value = "1000"),
                @Property(name = "MaxAutoRetriesNextServer", value = "2")
        },
        exportToArchaius = true
)
public interface IPFS extends Swarm {

    @Http(method = GET, uri = "/api/v0/version")
    RibbonRequest<ByteBuf> version();

    @Http(method = GET, uri = "/api/v0/cat?arg={multihash}")
    RibbonRequest<ByteBuf> cat(@Var("multihash") String multihash);

}

