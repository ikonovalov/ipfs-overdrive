/*
 *   Copyright (C) 2017 Igor Konovalov
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

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
