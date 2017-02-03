package ru.codeunited.ipfs;

import com.fasterxml.jackson.databind.type.MapType;
import com.google.gson.Gson;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.config.ConfigurationManager;
import com.netflix.ribbon.ClientOptions;
import com.netflix.ribbon.Ribbon;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by ikonovalov on 03/02/17.
 */
public interface RibbonEnvironment {

    Gson gson = new Gson();

    default IPFS configure() {
        Gson gson = new Gson();

        IPFS ipfs = new IPFSImpl(ClientOptions.create()
                .withMaxAutoRetriesNextServer(3)
                .withConfigurationBasedServerList("localhost:5001"));

        return ipfs;
    }

    default String stringify(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return new String(bytes, Charset.forName("UTF-8"));
    }

    default Map json(ByteBuf buf) {
        return json(stringify(buf));
    }

    default Map json(String s) {
        return gson.fromJson(s, Map.class);
    }

    default String json(Map map) {
        return gson.toJson(map);
    }
}
