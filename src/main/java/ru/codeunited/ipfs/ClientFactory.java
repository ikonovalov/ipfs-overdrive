package ru.codeunited.ipfs;

/**
 * OSS codeunited.ru
 * Created by ikonovalov on 06/03/17.
 */
public interface ClientFactory<OPTIONS> {

    int DEFAULT_API_SERVER_PORT = 5000;

    Client createLocal();

    Client create(OPTIONS opts);

}
