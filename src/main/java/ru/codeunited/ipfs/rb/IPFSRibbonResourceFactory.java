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
