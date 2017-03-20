package ru.codeunited.ipfs;

import com.netflix.ribbon.ClientOptions;
import ru.codeunited.ipfs.rb.IPFSFactoryRb;

/**
 * Created by ikonovalov on 03/02/17.
 */
public interface RibbonTestEnvironment extends JsonSupports{

    default IPFS configureLocal(int port) {
        IPFS ipfs = IPFSFactoryRb.createIpfs(ClientOptions.create()
                .withMaxAutoRetriesNextServer(3)
                .withConfigurationBasedServerList("localhost:" + port));

        return ipfs;
    }

    default IPFS configureLocal() {
        return configureLocal(5001);
    }


}
