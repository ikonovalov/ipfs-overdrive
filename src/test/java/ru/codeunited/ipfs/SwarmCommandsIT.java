package ru.codeunited.ipfs;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.observers.TestSubscriber;

import static java.util.concurrent.TimeUnit.SECONDS;

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
                .map(response -> response.get("Peers"))
                .doOnError(error -> log.error(error.getMessage(), error))
                .subscribe(mapTestSubscriber);

        mapTestSubscriber.awaitTerminalEvent(10, SECONDS);
        mapTestSubscriber.awaitValueCount(1, 10, SECONDS);
        log.info("Peers\n{}", mapTestSubscriber.getOnNextEvents() );
    }
}
