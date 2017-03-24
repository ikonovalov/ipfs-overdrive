package ru.codeunited.ipfs;

import com.google.common.base.MoreObjects;
import io.ipfs.multiaddr.MultiAddress;
import io.ipfs.multihash.Multihash;

import java.util.Set;

/**
 * OSS codeunited.ru
 * Created by ikonovalov on 20/03/17.
 */
public final class NodeId {

    private final Multihash ID;

    private final byte[] publicKey;

    private final Set<MultiAddress> addresses;

    private final String agentVersion;

    private final String protocolVersion;

    public NodeId(Multihash id, byte[] publicKey, Set<MultiAddress> addresses, String agentVersion, String protocolVersion) {
        ID = id;
        this.publicKey = publicKey;
        this.addresses = addresses;
        this.agentVersion = agentVersion;
        this.protocolVersion = protocolVersion;
    }

    public Multihash getID() {
        return ID;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public Set<MultiAddress> getAddresses() {
        return addresses;
    }

    public String getAgentVersion() {
        return agentVersion;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("ID", ID)
                .add("publicKey", "**" + publicKey.length * 8 + "**")
                .add("addresses", addresses)
                .add("agentVersion", agentVersion)
                .add("protocolVersion", protocolVersion)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NodeId nodeId = (NodeId) o;

        return ID.equals(nodeId.ID);
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }
}
