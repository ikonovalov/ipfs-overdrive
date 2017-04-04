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
