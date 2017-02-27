package ru.codeunited.ipfs;

import io.netty.buffer.ByteBuf;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.observers.TestSubscriber;

import java.util.concurrent.TimeUnit;

/**
 * OSS codeunited.ru
 * Created by ikonovalov on 04/02/17.
 */
public class RefsTest implements RibbonTestEnvironment {

    private Logger log = LoggerFactory.getLogger(RefsTest.class);

    @Test
    public void refs() throws InterruptedException {
        IPFS ipfs = configureLocal();
        TestSubscriber<ByteBuf> subscriber = new TestSubscriber<>();
        String mulHash = "QmXcqycvhph5YHWSGKSEFzvcNxAoH54KBUP1zGtTwfSLJS";
        ipfs.refs(mulHash).observe().doOnCompleted(()-> log.info("Multihash {} refs complete", mulHash)).subscribe(subscriber);
        subscriber.awaitTerminalEvent(2, TimeUnit.SECONDS);
        subscriber.assertCompleted();
        subscriber.assertValueCount(0);
    }
}
