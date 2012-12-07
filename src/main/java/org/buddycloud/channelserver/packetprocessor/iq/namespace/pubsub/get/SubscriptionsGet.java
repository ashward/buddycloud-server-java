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

package org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.get;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.JabberPubsub;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.PubSubElementProcessor;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.PubSubGet;
import org.buddycloud.channelserver.pubsub.model.NodeSubscription;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.resultsetmanagement.ResultSet;

public class SubscriptionsGet implements PubSubElementProcessor {

	private final BlockingQueue<Packet> outQueue;
	private final ChannelManager channelManager;
	
	private IQ result;
	private String node;
	private JID actorJid;
	private IQ requestIq;

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger
			.getLogger(SubscriptionsGet.class);

	public SubscriptionsGet(BlockingQueue<Packet> outQueue,
			ChannelManager channelManager) {
		this.outQueue = outQueue;
		this.channelManager = channelManager;
	}

	@Override
	public void process(Element elm, JID actorJID, IQ reqIQ, Element rsm)
			throws Exception {
		result = IQ.createResultIQ(reqIQ);
		actorJid = actorJID;
		requestIq = reqIQ;
		
		Element pubsub = result.setChildElement(PubSubGet.ELEMENT_NAME,
				elm.getNamespaceURI());
		Element subscriptions = pubsub.addElement("subscriptions");

		node = elm.attributeValue("node");

		if (actorJid == null) {
			actorJid = reqIQ.getFrom();
		}

		if (node == null) {
			getUserSubscriptions(subscriptions);
		} else {
			getNodeSubscriptions(subscriptions);
		}
	}

	private void getNodeSubscriptions(Element subscriptions)
			throws NodeStoreException, InterruptedException {
		if (false == channelManager.isLocalNode(node)) {
			makeRemoteRequest(new JID(node.split("/")[2]).getDomain());
		    return;
		}
		ResultSet<NodeSubscription> cur = channelManager
				.getNodeSubscriptions(node);
		subscriptions.addAttribute("node", node);

		for (NodeSubscription ns : cur) {

			subscriptions
					.addElement("subscription")
					.addAttribute("node", ns.getNodeId())
					.addAttribute("subscription",
							ns.getSubscription().toString())
					.addAttribute("jid", ns.getUser().toBareJID());
		}
		outQueue.put(result);
	}

	private void getUserSubscriptions(Element subscriptions)
			throws NodeStoreException, InterruptedException {
		if (false == channelManager.isLocalJID(actorJid)) {
			makeRemoteRequest(actorJid.getDomain());
			return;
		}
		// let's get all subscriptions.
		ResultSet<NodeSubscription> cur = channelManager
				.getUserSubscriptions(actorJid);
		
		for (NodeSubscription ns : cur) {
			subscriptions
					.addElement("subscription")
					.addAttribute("node", ns.getNodeId())
					.addAttribute("subscription",
							ns.getSubscription().toString())
					.addAttribute("jid", ns.getUser().toBareJID());
		}
		outQueue.put(result);
	}
	
	private void makeRemoteRequest(String to) throws InterruptedException {
		requestIq.setTo(to);
		Element actor = requestIq.getElement()
		    .element("pubsub")
		    .addElement("actor", JabberPubsub.NS_BUDDYCLOUD);
		actor.addText(requestIq.getFrom().toBareJID());
	    outQueue.put(requestIq);
	}
	
	@Override
	public boolean accept(Element elm) {
		return elm.getName().equals("subscriptions");
	}
}
