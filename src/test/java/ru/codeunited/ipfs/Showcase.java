package ru.codeunited.ipfs;

import com.google.gson.Gson;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.config.ConfigurationManager;
import com.netflix.ribbon.Ribbon;
import com.netflix.ribbon.RibbonRequest;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by ikonovalov on 01/02/17.
 */
public class Showcase {

    static final Logger LOG = LoggerFactory.getLogger(Showcase.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Gson gson = new Gson();
        int port = 5001;

        ConfigurationManager.getConfigInstance().setProperty("IPFS.ribbon." + CommonClientConfigKey.MaxAutoRetriesNextServer, "3");
        ConfigurationManager.getConfigInstance().setProperty("IPFS.ribbon." + CommonClientConfigKey.ListOfServers, "localhost:" + port);

        IPFS ipfsApi = Ribbon.from(IPFS.class);
        RibbonRequest<ByteBuf> version = ipfsApi.version();
        version
                .observe()
                .flatMap(byteBuf -> Observable.just(toString(byteBuf)))
                .reduce((s1, s2) -> s1 + s2)
                .subscribe(LOG::info);

        Future<ByteBuf> fResult = ipfsApi.version().queue();
        Map mResult = gson.fromJson(toString(fResult.get()), Map.class);
        LOG.info(mResult.toString());

        fResult = ipfsApi.cat("QmXcqycvhph5YHWSGKSEFzvcNxAoH54KBUP1zGtTwfSLJS").queue();
        mResult = gson.fromJson(toString(fResult.get()), Map.class);
        LOG.info(mResult.toString());

    }

    private static String toString(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        String s = new String(bytes, Charset.forName("UTF-8"));
        return s;
    }
}
