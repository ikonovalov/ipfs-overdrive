package ru.codeunited.ipfs.rb;

import com.netflix.ribbon.ClientOptions;
import ru.codeunited.ipfs.Client;
import ru.codeunited.ipfs.ClientFactory;

/**
 * OSS codeunited.ru
 * Created by ikonovalov on 06/03/17.
 */
public class ClientFactoryRb implements ClientFactory<ClientOptions> {

    protected ClientFactoryRb () {

    }

    public static ClientFactoryRb newInstance() {
        return new ClientFactoryRb();
    }

    @Override
    public Client createLocal() {
        String serverList = "localhost:" + DEFAULT_API_SERVER_PORT;
        return create(
                ClientOptions.create().withMaxAutoRetriesNextServer(3).withConfigurationBasedServerList(serverList)
        );
    }

    @Override
    public Client create(ClientOptions options) {
        return new Client(IPFSFactoryRb.createIpfs(options));
    }
}
