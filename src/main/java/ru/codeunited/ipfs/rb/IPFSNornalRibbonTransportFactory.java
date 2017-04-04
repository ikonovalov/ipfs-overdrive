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

package ru.codeunited.ipfs.rb;

import com.netflix.client.config.ClientConfigFactory;
import com.netflix.client.config.IClientConfig;
import com.netflix.ribbon.RibbonTransportFactory;
import com.netflix.ribbon.transport.netty.RibbonTransport;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.pipeline.PipelineConfigurator;
import io.reactivex.netty.pipeline.PipelineConfiguratorComposite;
import io.reactivex.netty.protocol.http.HttpObjectAggregationConfigurator;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientPipelineConfigurator;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;

import static io.reactivex.netty.protocol.http.HttpObjectAggregationConfigurator.DEFAULT_CHUNK_SIZE;

/**
 * OSS codeunited.ru
 * Created by ikonovalov on 01/04/17.
 */
public class IPFSNornalRibbonTransportFactory extends RibbonTransportFactory {

    protected IPFSNornalRibbonTransportFactory(ClientConfigFactory clientConfigFactory) {
        super(clientConfigFactory);
    }

    private final PipelineConfigurator<HttpClientResponse<ByteBuf>, HttpClientRequest<ByteBuf>>
            INFINITY_CHUNKED_PIPELINE = new PipelineConfiguratorComposite<>(
            new HttpClientPipelineConfigurator(),
            new HttpObjectAggregationConfigurator(DEFAULT_CHUNK_SIZE)
    );

    protected IPFSNornalRibbonTransportFactory() {
        super(ClientConfigFactory.DEFAULT);
    }

    @Override
    public HttpClient<ByteBuf, ByteBuf> newHttpClient(IClientConfig config) {
        return RibbonTransport.newHttpClient(INFINITY_CHUNKED_PIPELINE, config);
    }
}
