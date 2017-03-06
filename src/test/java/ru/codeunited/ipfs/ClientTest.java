package ru.codeunited.ipfs;

import com.netflix.ribbon.ClientOptions;
import org.junit.Test;
import ru.codeunited.ipfs.rb.ClientFactoryRb;

import static com.google.common.truth.Truth.assertThat;

/**
 * OSS codeunited.ru
 * Created by ikonovalov on 06/03/17.
 */
public class ClientTest {

    @Test
    public void createLocalClientOnRibbon() {
        ClientFactory<ClientOptions> clientFactory = ClientFactoryRb.newInstance();
        Client client = clientFactory.createLocal();
        assertThat(client).isNotNull();
    }
}