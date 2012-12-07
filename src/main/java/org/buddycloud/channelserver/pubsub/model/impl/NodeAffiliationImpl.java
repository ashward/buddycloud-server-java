/*
 * Buddycloud Channel Server
 * http://buddycloud.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.buddycloud.channelserver.pubsub.model.impl;

import org.buddycloud.channelserver.pubsub.affiliation.Affiliations;
import org.buddycloud.channelserver.pubsub.model.NodeAffiliation;
import org.xmpp.packet.JID;

/**
 * Basic implementation of a NodeAffiliation
 */
public class NodeAffiliationImpl implements NodeAffiliation {

	private final JID user;
	private final String nodeId;
	private final Affiliations affiliation;

	/**
	 * Constructs a new object to represent a user's affiliation with a node
	 * 
	 * @param nodeId
	 * @param user
	 */
	public NodeAffiliationImpl(final String nodeId, final JID user,
			final Affiliations affiliation) {
		this.nodeId = nodeId;
		if (user.getResource() == null) {
			this.user = user;
		} else {
			this.user = new JID(user.toBareJID());
		}
		this.affiliation = affiliation;
	}

	public JID getUser() {
		return user;
	}

	public String getNodeId() {
		return nodeId;
	}

	public Affiliations getAffiliation() {
		return affiliation;
	}

	@Override
	public String toString() {
		return "NodeAffiliationImpl [user=" + user + ", nodeId=" + nodeId
				+ ", affiliation=" + affiliation + "]";
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((affiliation == null) ? 0 : affiliation.hashCode());
		result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof NodeAffiliation))
			return false;
		NodeAffiliation other = (NodeAffiliation) obj;
		if (affiliation != other.getAffiliation())
			return false;
		if (nodeId == null) {
			if (other.getNodeId() != null)
				return false;
		} else if (!nodeId.equals(other.getNodeId()))
			return false;
		if (user == null) {
			if (other.getUser() != null)
				return false;
		} else if (!user.equals(other.getUser()))
			return false;
		return true;
	}

	@Override
	public String getUID() {
		return String.valueOf(hashCode());
	}
}
