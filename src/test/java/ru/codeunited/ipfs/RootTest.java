package ru.codeunited.ipfs;

import io.netty.buffer.ByteBuf;
import org.junit.Test;
import rx.observers.TestSubscriber;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static rx.Observable.just;

/**
 * Created by ikonovalov on 03/02/17.
 */

public class RootTest implements RibbonEnvironment {

    @Test
    public void version() {
        IPFS ipfs = configure();

        ByteBuf ver = ipfs.version().execute();
        Map m = json(ver);

        assertThat(m, notNullValue());
        assertThat(m.get("Version"), notNullValue());
        assertThat(m.get("Version").toString().length(), not(0));
    }

    @Test
    public void commands() throws InterruptedException {
        IPFS ipfs = configure();
        TestSubscriber<Map> mapTestSubscriber = new TestSubscriber<>();
        Consumer<Map> rootIsIPFS = m -> assertThat(m.get("Name"), is("ipfs"));
        ipfs.commands().observe().flatMap(buf -> just(json(buf))).subscribe(mapTestSubscriber);
        mapTestSubscriber.awaitTerminalEventAndUnsubscribeOnTimeout(5, TimeUnit.SECONDS);
        mapTestSubscriber.getOnNextEvents().stream().findFirst().ifPresent(rootIsIPFS);
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
    public void add() {
        IPFS ipfs = configure();
        ipfs.add(new ByteArrayInputStream(new byte[1024]));
    }

}
