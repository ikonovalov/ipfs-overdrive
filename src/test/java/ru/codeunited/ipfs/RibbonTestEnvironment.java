package ru.codeunited.ipfs;

import com.netflix.ribbon.ClientOptions;
import ru.codeunited.ipfs.rb.IPFSFactoryRb;

/**
 * Created by ikonovalov on 03/02/17.
 */
public interface RibbonTestEnvironment extends JsonSupports {


    int DEFAULT_IPFS_API_PORT = 5001;

    default IPFS configureLocal(int port) {
        IPFS ipfs = IPFSFactoryRb.createIpfs(ClientOptions.create()
                .withMaxAutoRetriesNextServer(3)
                .withFollowRedirects(false)
                .withConfigurationBasedServerList("localhost:" + port));

        return ipfs;
    }

    default IPFS configureLocal() {
        return configureLocal(DEFAULT_IPFS_API_PORT);
    }


}
