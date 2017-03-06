package ru.codeunited.ipfs;

import com.netflix.ribbon.ClientOptions;
import org.junit.Test;
import ru.codeunited.ipfs.rb.ClientFactoryRb;

/**
 * OSS codeunited.ru
 * Created by ikonovalov on 06/03/17.
 */
class ClientTest {

    @Test
    public void createLocalClientOnRibbon() {
        ClientFactory<ClientOptions> clientFactory = ClientFactoryRb.newInstance();

    }
}