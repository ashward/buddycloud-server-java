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

package org.buddycloud.channelserver.packetprocessor.message.event;

import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.Assert;

import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.packetHandler.iq.IQTestHandler;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.JabberPubsub;
import org.buddycloud.channelserver.pubsub.model.NodeSubscription;
import org.buddycloud.channelserver.pubsub.model.impl.NodeSubscriptionImpl;
import org.buddycloud.channelserver.pubsub.subscription.Subscriptions;
import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError.Condition;
import org.xmpp.resultsetmanagement.ResultSet;
import org.xmpp.resultsetmanagement.ResultSetImpl;

public class ItemsProcessorTest extends IQTestHandler {

	private Message message;
	private ItemsProcessor itemsProcessor;
	private BlockingQueue<Packet> queue = new LinkedBlockingQueue<Packet>();
	private ChannelManager channelManager;

	@Before
	public void setUp() throws Exception {

		JID jid = new JID("juliet@shakespeare.lit");
		Properties configuration = new Properties();
		configuration.setProperty("server.domain.channels", "channels.shakespeare.lit");
		channelManager = Mockito.mock(ChannelManager.class);

		ArrayList<NodeSubscription> subscribers = new ArrayList<NodeSubscription>();
		subscribers.add(new NodeSubscriptionImpl(
				"/users/romeo@shakespeare.lit/posts", jid,
				Subscriptions.subscribed));
		Mockito.doReturn(new ResultSetImpl<NodeSubscription>(subscribers)).when(channelManager)
				.getNodeSubscriptions(Mockito.anyString());
		Mockito.when(channelManager.isLocalNode(Mockito.anyString()))
				.thenReturn(false);

		itemsProcessor = new ItemsProcessor(queue, configuration, channelManager);

		message = new Message();
		message.setType(Message.Type.headline);
		Element event = message.addChildElement("event",
				JabberPubsub.NS_PUBSUB_EVENT);
		Element items = event.addElement("items");
		Element item = items.addElement("item");

		items.addAttribute("node", "/users/romeo@shakespeare.lit/posts");
		item.addAttribute("id", "publish:1");

	}

	@Test
	public void testLocalNodeEventDoesNotSendNotiifcations() throws Exception {
		Mockito.when(channelManager.isLocalNode(Mockito.anyString()))
				.thenReturn(true);
		itemsProcessor.process(message);
		Assert.assertEquals(0, queue.size());
	}

	@Test(expected=NodeStoreException.class)
	public void testNodeStoreExceptionIsThrown() throws Exception {
		Mockito.doThrow(new NodeStoreException()).when(channelManager)
		   .getNodeSubscriptions(Mockito.anyString());
		itemsProcessor.process(message);
	}

	@Test(expected=NullPointerException.class)
	public void testConfigurationValueNotSetThrowsException() throws Exception {
		itemsProcessor.setConfiguration(new Properties());
	    itemsProcessor.process(message);	
	}
	
	@Test
	public void testNotificationsAreForwarded() throws Exception {
		itemsProcessor.process(message);
		Assert.assertEquals(1, queue.size());
	}
}