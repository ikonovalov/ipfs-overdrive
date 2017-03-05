package ru.codeunited.ipfs;

import java.util.Properties;

/**
 * OSS codeunited.ru
 * Created by ikonovalov on 06/03/17.
 */
public abstract class ClientFactory {

    public abstract Client createDefault();

    public abstract Client create(Properties properties);
}
