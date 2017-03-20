package ru.codeunited.ipfs;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * OSS codeunited.ru
 * Created by ikonovalov on 20/03/17.
 */
public interface JsonSupports {

    Gson gson = new Gson();

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
