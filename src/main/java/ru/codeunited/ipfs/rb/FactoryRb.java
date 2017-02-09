package ru.codeunited.ipfs.rb;

import com.netflix.ribbon.ClientOptions;

/**
 * OSS codeunited.ru
 * Created by ikonovalov on 09/02/17.
 */
public class FactoryRb {

    public static InterPlanetaryFileSystemRb createIpfs(ClientOptions options) {
        return new InterPlanetaryFileSystemRb(options);
    }

}
