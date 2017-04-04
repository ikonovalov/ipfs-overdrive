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

import com.netflix.ribbon.ClientOptions;
import ru.codeunited.ipfs.Client;
import ru.codeunited.ipfs.ClientFactory;

/**
 * OSS codeunited.ru
 * Created by ikonovalov on 06/03/17.
 */
public class ClientFactoryRb implements ClientFactory<ClientOptions> {

    protected ClientFactoryRb () {

    }

    public static ClientFactoryRb newInstance() {
        return new ClientFactoryRb();
    }

    @Override
    public Client createLocal() {
        String serverList = "localhost:" + DEFAULT_API_SERVER_PORT;
        return create(
                ClientOptions.create().withMaxAutoRetriesNextServer(3).withConfigurationBasedServerList(serverList)
        );
    }

    @Override
    public Client create(ClientOptions options) {
        return new Client(IPFSFactoryRb.createIpfs(options));
    }
}
