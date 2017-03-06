package ru.codeunited.ipfs.rb;

import com.netflix.ribbon.ClientOptions;

/**
 * OSS codeunited.ru
 * Created by ikonovalov on 09/02/17.
 */
public class IPFSFactoryRb {

    protected IPFSFactoryRb() {

    }

    public static IPFSRb createIpfs(ClientOptions options) {
        return new IPFSRb(options);
    }

}
