package ru.codeunited.ipfs;

import com.netflix.ribbon.RibbonRequest;
import io.netty.buffer.ByteBuf;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.observers.TestSubscriber;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.google.common.truth.Truth.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;
import static rx.Observable.just;

/**
 * Created by ikonovalov on 03/02/17.
 */

public class RootCommandsIT implements RibbonTestEnvironment {

    public static final String MULTHASH = "QmXcqycvhph5YHWSGKSEFzvcNxAoH54KBUP1zGtTwfSLJS";

    private Logger log = LoggerFactory.getLogger(RootCommandsIT.class);

    private int IPFS_PORT = DEFAULT_IPFS_API_PORT;

    @Test
    public void id() throws InterruptedException, ExecutionException, TimeoutException {
        IPFS ipfs = configureLocal();
        Future<ByteBuf> bufFuture = ipfs.id().queue();
        Map<String, Object> id = json(bufFuture.get(1, SECONDS));
        assertNotEquals(id, "Incoming IPFS node ID is null");
        id.entrySet().forEach(entry -> log.info("{} = {}", entry.getKey(), entry.getValue()));
    }

    @Test
    public void version() {
        IPFS ipfs = configureLocal(IPFS_PORT);

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
    public void catSingle() {
        IPFS ipfs = configureLocal(IPFS_PORT);
        ByteBuf buffer = ipfs.catSingle(MULTHASH).execute();
        Map<String, Object> file = json(buffer);
        assertNotNull(file);
    }

    @Test
    public void cat() throws InterruptedException {
        IPFS ipfs = configureLocal(IPFS_PORT);
        StringBuffer stringBuilder = new StringBuffer();
        CountDownLatch latch = new CountDownLatch(1);
        ipfs.cat(MULTHASH).observe().subscribe(
                chunk -> stringBuilder.append(stringify(chunk)),
                error -> {},
                () -> latch.countDown()
        );
        latch.await(5, SECONDS);
        Map<String, Object> file = json(stringBuilder.toString());
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
    public void addAndCatLargeFile() throws IOException, InterruptedException {
        IPFS ipfs = configureLocal(IPFS_PORT);
         TestSubscriber<ByteBuf> subscriber = new TestSubscriber<>();
        int _10Mb = 1024 * 1024 * 10;
        byte[] bytes = new byte[_10Mb];
        Arrays.fill(bytes, (byte) 'Z');
        // first and last markers
        bytes[0] = 'A';
        bytes[bytes.length - 1] = 'X';
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        RibbonRequest<ByteBuf> request = ipfs.add(bais);
        request.observe().subscribe(subscriber);

        subscriber.awaitTerminalEvent(10, SECONDS);
        List<ByteBuf> onNextEvents = subscriber.getOnNextEvents();
        log.info("Big upload total events: {}", onNextEvents.size());
        AtomicReference<String> atomicHash = new AtomicReference<>();
        onNextEvents.stream().findFirst().ifPresent(bbHash -> {
                    String hash = (String) json(bbHash).get("Hash");
                    log.info("Uploaded hash: {}", hash);
                    atomicHash.set(hash);
                }
        );

        RibbonRequest<ByteBuf> getRequest = ipfs.cat(atomicHash.get());
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger aInt = new AtomicInteger(0);
        getRequest.observe()
                .subscribe(
                        chunk -> {
                            int readableBytes = chunk.readableBytes();
                            aInt.getAndAdd(readableBytes);
                        },
                        error -> error.printStackTrace(),
                        () -> latch.countDown()
                );
        latch.await(10, SECONDS);
        if (latch.getCount() == 0) {
            log.info("Success!");
            assertThat(_10Mb).isEqualTo(aInt.get());
        } else {
            String message = "Transfer incomplete";
            log.error(message);
            assertTrue(false, message);
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

    @Test
    public void ls() {
        final IPFS ipfs = configureLocal(IPFS_PORT);
        final TestSubscriber<ByteBuf> subscriber = new TestSubscriber<>();
        ipfs.ls(MULTHASH).observe().subscribe(subscriber);
        subscriber.awaitValueCount(1, 2, SECONDS);
        subscriber.awaitTerminalEvent(5, SECONDS);
        assertThat(json(subscriber.getOnNextEvents().get(0))).containsKey("Objects");
    }

}
