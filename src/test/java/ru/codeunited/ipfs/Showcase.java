package ru.codeunited.ipfs;

import com.google.gson.Gson;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.config.ConfigurationManager;
import com.netflix.ribbon.ClientOptions;
import com.netflix.ribbon.Ribbon;
import com.netflix.ribbon.RibbonRequest;
import com.netflix.ribbon.http.HttpRequestTemplate;
import com.netflix.ribbon.http.HttpResourceGroup;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static rx.Observable.just;

/**
 * Created by ikonovalov on 01/02/17.
 */
public class Showcase {

    static final Logger LOG = LoggerFactory.getLogger(Showcase.class);

    static Gson gson = new Gson();

    public static void main(String[] args) throws ExecutionException, InterruptedException, URISyntaxException, TimeoutException {

        HttpResourceGroup httpResourceGroup = Ribbon.createHttpResourceGroup("ipfs",
                ClientOptions.create()
                        .withMaxAutoRetriesNextServer(3)
                        .withConfigurationBasedServerList("localhost:5001"));

        HttpRequestTemplate.Builder<ByteBuf> resourceGroup = httpResourceGroup.newTemplateBuilder("ipfs_version", ByteBuf.class);
        HttpRequestTemplate<ByteBuf> registerMovieTemplate =
                resourceGroup
                .withMethod("POST")
                .withUriTemplate("/api/v0/version")
                .build();

        RibbonRequest<ByteBuf> req = registerMovieTemplate.requestBuilder()
                //.withRawContentSource(Observable.just(Movie.ORANGE_IS_THE_NEW_BLACK), new RxMovieTransformer())
                .build();

        ByteBuf b = req.queue().get(200, TimeUnit.MILLISECONDS);
        Map m = gson.fromJson(toString(b), Map.class);
        System.out.println(m);

    }

    private static void works() throws ExecutionException, InterruptedException {
        ConfigurationManager.getConfigInstance().setProperty("IPFS.ribbon." + CommonClientConfigKey.MaxAutoRetriesNextServer, "3");
        ConfigurationManager.getConfigInstance().setProperty("IPFS.ribbon." + CommonClientConfigKey.ListOfServers, "localhost:5001");

        IPFS ipfsApi = Ribbon.from(IPFS.class);
        RibbonRequest<ByteBuf> version = ipfsApi.version();
        version
                .observe()
                .flatMap(byteBuf -> just(toString(byteBuf)))
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
