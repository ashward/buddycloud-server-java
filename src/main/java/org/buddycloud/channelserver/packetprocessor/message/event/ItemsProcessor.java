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

import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.packetprocessor.PacketProcessor;
import org.buddycloud.channelserver.pubsub.model.NodeSubscription;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.resultsetmanagement.ResultSet;

public class ItemsProcessor implements PacketProcessor<Message> {

	private Message message;
	private String node;
	private ChannelManager channelManager;
	private Properties configuration;
	private BlockingQueue<Packet> outQueue;

	public ItemsProcessor(BlockingQueue<Packet> outQueue,
			Properties configuration, ChannelManager channelManager) {
		this.channelManager = channelManager;
		setConfiguration(configuration);
		this.outQueue = outQueue;
	}
	
	public void setConfiguration(Properties configuration) {
		this.configuration = configuration;
	}

	@Override
	public void process(Message packet) throws Exception {

		message = packet;
		node = message.getElement().element("event").element("items")
				.attributeValue("node");

		if (false == channelManager.isLocalNode(node)) {
			sendLocalNotifications();
		}
	}

	private void sendLocalNotifications() throws Exception {
		ResultSet<NodeSubscription> subscribers = channelManager
				.getNodeSubscriptions(node);
		for (NodeSubscription subscriber : subscribers) {
			message.setTo(subscriber.getUser());
			message.setFrom(new JID(configuration
					.getProperty("server.domain.channels")));
			outQueue.put(message.createCopy());
		}
	}
}
