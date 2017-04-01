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
