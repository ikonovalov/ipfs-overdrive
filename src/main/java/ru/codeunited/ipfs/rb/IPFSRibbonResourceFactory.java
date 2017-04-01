package ru.codeunited.ipfs.rb;

import com.netflix.client.config.ClientConfigFactory;
import com.netflix.ribbon.DefaultResourceFactory;
import com.netflix.ribbon.RibbonTransportFactory;
import com.netflix.ribbon.proxy.processor.AnnotationProcessorsProvider;

/**
 * OSS codeunited.ru
 * Created by ikonovalov on 01/04/17.
 */
public class IPFSRibbonResourceFactory extends DefaultResourceFactory {

    protected IPFSRibbonResourceFactory(ClientConfigFactory clientConfigFactory, RibbonTransportFactory transportFactory, AnnotationProcessorsProvider annotationProcessorsProvider) {
        super(clientConfigFactory, transportFactory, annotationProcessorsProvider);
    }

    protected IPFSRibbonResourceFactory(ClientConfigFactory clientConfigFactory, RibbonTransportFactory transportFactory) {
        super(clientConfigFactory, transportFactory);
    }


    public static IPFSRibbonResourceFactory normalResourceFactory() {
        return new IPFSRibbonResourceFactory(
                ClientConfigFactory.DEFAULT, new IPFSNornalRibbonTransportFactory(ClientConfigFactory.DEFAULT)
        );
    }

    public static IPFSRibbonResourceFactory oversizeReourceFactory() {
        return new IPFSRibbonResourceFactory(
                ClientConfigFactory.DEFAULT, new IPFSOversizeRibbonTransportFactory(ClientConfigFactory.DEFAULT)
        );
    }
}
