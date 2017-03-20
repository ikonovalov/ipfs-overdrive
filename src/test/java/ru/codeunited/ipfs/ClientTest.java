package ru.codeunited.ipfs;

import com.netflix.ribbon.ClientOptions;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.codeunited.ipfs.rb.ClientFactoryRb;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * OSS codeunited.ru
 * Created by ikonovalov on 06/03/17.
 */
public class ClientTest {

    private final Logger log = LoggerFactory.getLogger(ClientTest.class);

    @Test
    public void createLocalClientOnRibbon() {
        ClientFactory<ClientOptions> clientFactory = ClientFactoryRb.newInstance();
        Client client = clientFactory.createLocal();
        assertThat(client).isNotNull();
    }

    @Test
    public void extractSwarmPeers() {
        ClientFactory<ClientOptions> clientFactory = ClientFactoryRb.newInstance();
        Client client = clientFactory.createLocal();

        List<Peer> peers = client.getPeers();
        peers.stream().map(Peer::toString).forEach(log::info);
    }
}