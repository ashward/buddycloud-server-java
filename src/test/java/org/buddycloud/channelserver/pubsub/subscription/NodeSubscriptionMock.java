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

package org.buddycloud.channelserver.pubsub.subscription;

import org.buddycloud.channelserver.pubsub.affiliation.Affiliations;
import org.buddycloud.channelserver.pubsub.model.NodeSubscription;
import org.xmpp.packet.JID;

public class NodeSubscriptionMock implements NodeSubscription {
	private JID subscriber;
	private Affiliations affiliation;
	private Subscriptions subscription;
	private String foreignChannelServer;

	public void setBareJID(JID jid) {
		this.subscriber = jid;
	}

	public void setAffiliation(Affiliations affiliation) {
		this.affiliation = affiliation;
	}

	public void setSubscription(Subscriptions subscription) {
		this.subscription = subscription;
	}

	public void setForeignChannelServer(String foreignChannelServer) {
		this.foreignChannelServer = foreignChannelServer;
	}

	public NodeSubscriptionMock(JID jid) {
		subscriber = jid;
	}

	@Override
	public Subscriptions getSubscription() {
		// TODO Auto-generated method stub
		return subscription;
	}

	@Override
	public JID getUser() {
		// TODO Auto-generated method stub
		return subscriber;
	}

	@Override
	public JID getListener() {
		// TODO Auto-generated method stub
		return subscriber;
	}

	@Override
	public String getNodeId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUID() {
		return subscriber.toString();
	}
}