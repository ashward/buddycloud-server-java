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

package org.buddycloud.channelserver.db.mock;

import java.util.Map;

import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.pubsub.affiliation.Affiliations;
import org.buddycloud.channelserver.pubsub.model.NodeAffiliation;
import org.buddycloud.channelserver.pubsub.model.NodeItem;
import org.buddycloud.channelserver.pubsub.model.NodeSubscription;
import org.xmpp.packet.JID;
import org.xmpp.resultsetmanagement.ResultSet;

public class Mock implements ChannelManager {

	// Store last provided configuration
	private Map<String, String> conf;
	// Store last provided node subscription
	private NodeSubscription nodeSubscription;
	// Store last provided node affiliation
	private Affiliations nodeAffiliation;

	/**
	 * Helper methods
	 */
	public NodeSubscription getLastUserNodeSubscription() {
		return nodeSubscription;
	}

	public Affiliations getLastUserNodeAffiliation() {
		return nodeAffiliation;
	}

	/**
	 * Standard methods
	 */

	@Override
	public void createNode(JID owner, String nodeId,
			Map<String, String> nodeConf) throws NodeStoreException {

		conf = nodeConf;
	}

	@Override
	public void setNodeConfValue(String nodeId, String key, String value)
			throws NodeStoreException {

	}

	@Override
	public void setNodeConf(String nodeId, Map<String, String> conf)
			throws NodeStoreException {

	}

	@Override
	public String getNodeConfValue(String nodeId, String key)
			throws NodeStoreException {

		return null;
	}

	@Override
	public Map<String, String> getNodeConf(String nodeId)
			throws NodeStoreException {

		return conf;
	}

	@Override
	public boolean nodeExists(String nodeId) throws NodeStoreException {

		return false;
	}

	@Override
	public void setUserAffiliation(String nodeId, JID user,
			Affiliations affiliation) throws NodeStoreException {

		nodeAffiliation = affiliation;
	}

	@Override
	public void addUserSubscription(NodeSubscription subscription)
			throws NodeStoreException {

		nodeSubscription = subscription;
	}

	@Override
	public NodeAffiliation getUserAffiliation(String nodeId, JID user)
			throws NodeStoreException {

		return null;
	}

	@Override
	public ResultSet<NodeAffiliation> getUserAffiliations(JID user)
			throws NodeStoreException {

		return null;
	}

	@Override
	public ResultSet<NodeAffiliation> getNodeAffiliations(String nodeId)
			throws NodeStoreException {

		return null;
	}

	@Override
	public ResultSet<NodeSubscription> getUserSubscriptions(JID user)
			throws NodeStoreException {

		return null;
	}

	@Override
	public ResultSet<NodeSubscription> getNodeSubscriptions(String nodeId)
			throws NodeStoreException {

		return null;
	}

	@Override
	public NodeSubscription getUserSubscription(String nodeId, JID user)
			throws NodeStoreException {

		return null;
	}

	@Override
	public ResultSet<NodeItem> getNodeItems(String nodeId, String afterItemId,
			int count) throws NodeStoreException {

		return null;
	}

	@Override
	public ResultSet<NodeItem> getNodeItems(String nodeId)
			throws NodeStoreException {

		return null;
	}

	@Override
	public int countNodeItems(String nodeId) throws NodeStoreException {

		return 0;
	}

	@Override
	public NodeItem getNodeItem(String nodeId, String nodeItemId)
			throws NodeStoreException {

		return null;
	}

	@Override
	public void addNodeItem(NodeItem item) throws NodeStoreException {

	}

	@Override
	public void updateNodeItem(NodeItem item) throws NodeStoreException {

	}

	@Override
	public void deleteNodeItemById(String nodeId, String nodeItemId)
			throws NodeStoreException {

	}

	@Override
	public void close() throws NodeStoreException {

	}

	@Override
	public Transaction beginTransaction() throws NodeStoreException {

		return null;
	}

	@Override
	public void createPersonalChannel(JID ownerJID) throws NodeStoreException {

	}

	@Override
	public boolean isLocalNode(String nodeId) {

		return true;
	}

	@Override
	public boolean isLocalJID(JID jid) {

		return false;
	}

	@Override
	public void addRemoteNode(String node) throws NodeStoreException {

	}

	@Override
	public ResultSet<NodeSubscription> getNodeSubscriptionListeners(
			String nodeId) throws NodeStoreException {
		return null;
	}

	@Override
	public int getMaximumStanzaSize() {
		return 0;
	}
}