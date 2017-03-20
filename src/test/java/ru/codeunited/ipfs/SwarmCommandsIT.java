package ru.codeunited.ipfs;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.joining;

/**
 * OSS codeunited.ru
 * Created by ikonovalov on 14/02/17.
 */
public class SwarmCommandsIT implements RibbonTestEnvironment {

    private Logger log = LoggerFactory.getLogger(SwarmCommandsIT.class);

    @Test
    public void showPeers() {
        IPFS ipfs = configureLocal(5001);
        TestSubscriber mapTestSubscriber = new TestSubscriber();
        ipfs.swarm().peers().observe()
                .map(this::json)
                .map(response -> response.get("Peers")).cast(Iterable.class)
                .flatMap(Observable::from)
                .subscribe(mapTestSubscriber);

        mapTestSubscriber.awaitTerminalEvent(10, SECONDS);
        mapTestSubscriber.awaitValueCount(1, 10, SECONDS);
        List peers = mapTestSubscriber.getOnNextEvents();
        log.info("Total peers {}", peers.size());
        log.info("Peers\n{}", peers.stream().map(Object::toString).collect(joining("\n")));
    }
}
