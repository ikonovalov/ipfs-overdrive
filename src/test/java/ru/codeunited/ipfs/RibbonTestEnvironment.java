package ru.codeunited.ipfs;

import com.google.gson.Gson;
import com.netflix.ribbon.ClientOptions;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by ikonovalov on 03/02/17.
 */
public interface RibbonTestEnvironment {

    Gson gson = new Gson();

    default IPFS configure(int port) {
        IPFS ipfs = new InterPlanetaryFileSystemRibbon(ClientOptions.create()
                .withMaxAutoRetriesNextServer(3)
                .withConfigurationBasedServerList("localhost:" + port));

        return ipfs;
    }

    default IPFS configure() {
        return configure(5001);
    }

    default String stringify(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return new String(bytes, Charset.forName("UTF-8"));
    }

    default Map<String, Object> json(ByteBuf buf) {
        return json(stringify(buf));
    }

    default Map<String, Object> json(String s) {
        return gson.fromJson(s, Map.class);
    }

    default String json(Map map) {
        return gson.toJson(map);
    }
}
