package ru.codeunited.ipfs;

import com.netflix.ribbon.RibbonRequest;
import io.netty.buffer.ByteBuf;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.observers.TestSubscriber;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static rx.Observable.just;

/**
 * Created by ikonovalov on 03/02/17.
 */

public class RootCommandsTest implements RibbonTestEnvironment {

    private Logger log = LoggerFactory.getLogger(RootCommandsTest.class);

    @Test
    public void version() {
        IPFS ipfs = configure();

        ByteBuf ver = ipfs.version().execute();
        Map m = json(ver);

        assertThat(m, notNullValue());
        String version = String.valueOf(m.get("Version"));
        log.info("IPFS version {}", version);

        assertThat(version, notNullValue());
        assertThat(version.length(), not(0));
    }

    @Test
    public void commands() throws InterruptedException {
        IPFS ipfs = configure();
        TestSubscriber<Map> mapTestSubscriber = new TestSubscriber<>();
        Consumer<Map> rootIsIPFS = m -> assertThat(m.get("Name"), is("ipfs"));
        ipfs.commands().observe().flatMap(buf -> just(json(buf))).subscribe(mapTestSubscriber);
        mapTestSubscriber.awaitTerminalEventAndUnsubscribeOnTimeout(5, TimeUnit.SECONDS);
        mapTestSubscriber.getOnNextEvents().stream().findFirst().ifPresent(rootIsIPFS);
        mapTestSubscriber.getOnErrorEvents().stream().findFirst().ifPresent(throwable -> {
            throw new RuntimeException("Error events detected. ", throwable);
        });
    }

    @Test
    public void cat() {
        IPFS ipfs = configure();
        ByteBuf buffer = ipfs.cat("QmXcqycvhph5YHWSGKSEFzvcNxAoH54KBUP1zGtTwfSLJS").execute();
        Map<String, Object> file= json(buffer);
        assertThat(file, notNullValue());
        System.out.println(file);
    }

    @Test
    public void add() throws IOException {
        IPFS ipfs = configure(5001);
        TestSubscriber<ByteBuf> subscriber = new TestSubscriber<>();

        try (final InputStream is = new FileInputStream("/mnt/u110/ethereum/pnet1/CustomGenesis.json")) {
            RibbonRequest<ByteBuf> request = ipfs.add(is);
            request.observe()
                    .doOnError(t -> log.error("* {}", t.getMessage()))
                    .doOnNext(b -> log.info("** {}", stringify(b)))
                    .doOnCompleted(() -> log.info("Request complete"))
                    .subscribe(subscriber);

            log.info(subscriber.awaitValueCount(1, 5, TimeUnit.SECONDS) ? "OnNext happens" : "OnNext NOT received");
            subscriber.awaitTerminalEvent(7, TimeUnit.SECONDS);
        }
    }

}
