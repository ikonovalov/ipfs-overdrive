package ru.codeunited.ipfs;

import com.netflix.ribbon.RibbonRequest;
import io.netty.buffer.ByteBuf;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.observers.TestSubscriber;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;
import static rx.Observable.just;

/**
 * Created by ikonovalov on 03/02/17.
 */

public class RootCommandsIT implements RibbonTestEnvironment {

    public static final String MULTHASH = "QmXcqycvhph5YHWSGKSEFzvcNxAoH54KBUP1zGtTwfSLJS";
    private Logger log = LoggerFactory.getLogger(RootCommandsIT.class);

    private int IPFS_PORT = 5001;

    @Test
    public void version() {
        IPFS ipfs = configureLocal();

        ByteBuf ver = ipfs.version().execute();
        Map m = json(ver);

        assertNotNull(m, "Incoming JSON is null or empty");
        String version = String.valueOf(m.get("Version"));
        log.info("IPFS version {}", version);

        assertNotNull(version, "Version is null");
        assertNotEquals(0, version.length(), "Version is empty");
    }

    @Test
    public void commands() throws InterruptedException {
        IPFS ipfs = configureLocal(IPFS_PORT);
        TestSubscriber<Map> mapTestSubscriber = new TestSubscriber<>();
        Consumer<Map> rootIsIPFS = m -> assertEquals("ipfs", m.get("Name"));
        ipfs.commands().observe().flatMap(buf -> just(json(buf))).subscribe(mapTestSubscriber);
        mapTestSubscriber.awaitTerminalEventAndUnsubscribeOnTimeout(5, SECONDS);
        mapTestSubscriber.getOnNextEvents().stream().findFirst().ifPresent(rootIsIPFS);
        mapTestSubscriber.getOnErrorEvents().stream().findFirst().ifPresent(throwable -> {
            throw new RuntimeException("Error events detected. ", throwable);
        });
    }

    @Test
    public void cat() {
        IPFS ipfs = configureLocal(IPFS_PORT);
        ByteBuf buffer = ipfs.cat(MULTHASH).execute();
        Map<String, Object> file = json(buffer);
        assertNotNull(file);
    }

    @Test
    public void get() {
        IPFS ipfs = configureLocal(IPFS_PORT);
        ByteBuf buffer = ipfs.get(MULTHASH).execute();
        String file = stringify(buffer);
        assertNotNull(file);
        assertTrue(() -> file.length() > 0);
    }

    @Test
    public void add() throws IOException {
        IPFS ipfs = configureLocal(IPFS_PORT);
        TestSubscriber<ByteBuf> subscriber = new TestSubscriber<>();

        try (final InputStream is = new FileInputStream("/mnt/u110/ethereum/pnet1/CustomGenesis.json")) {
            RibbonRequest<ByteBuf> request = ipfs.add(is);
            request.observe()
                    .doOnError(t -> log.error("* {}", t.getMessage()))
                    .doOnCompleted(() -> log.info("Request complete"))
                    .subscribe(subscriber);

            log.info(subscriber.awaitValueCount(1, 5, SECONDS) ? "OnNext happens" : "OnNext NOT received");
            subscriber.awaitTerminalEvent(7, SECONDS);
            List<ByteBuf> onNextEvents = subscriber.getOnNextEvents();
            onNextEvents.stream().findFirst().map(e -> stringify(e)).map(se -> json(se)).ifPresent(json -> assertTrue(json.containsKey("Hash")));
        }
    }

    @Test
    @DisplayName("Add and cat IPFS commands")
    public void addAndCat() throws IOException, InterruptedException {
        final IPFS ipfs = configureLocal(IPFS_PORT);
        final CountDownLatch latch = new CountDownLatch(1);
        final TestSubscriber<ByteBuf> subscriber = new TestSubscriber<>();
        try (final InputStream is = new FileInputStream("/mnt/u110/ethereum/pnet1/CustomGenesis.json")) {
            ipfs.add(is).observe().map(this::stringify).reduce((s1, s2) -> s1 + s2).subscribe(s -> {
                Map<String, ?> resp = json(s);
                String hash = (String) resp.get("Hash");
                log.info("Hash {}", hash);
                latch.countDown();
                ipfs.cat(hash).observe().subscribe(subscriber);
            });
            latch.await(5, SECONDS);
        }

        subscriber.awaitValueCount(1, 5, SECONDS);
        subscriber.getOnNextEvents().stream().findFirst().map(this::json).map(java.lang.Object::toString).ifPresent(log::info);
        subscriber.awaitTerminalEventAndUnsubscribeOnTimeout(5, SECONDS);

    }

}
