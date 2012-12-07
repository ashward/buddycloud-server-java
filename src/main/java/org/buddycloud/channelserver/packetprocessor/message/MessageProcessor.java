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

package org.buddycloud.channelserver.packetprocessor.message;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.packetprocessor.PacketProcessor;
import org.buddycloud.channelserver.packetprocessor.message.event.ItemsProcessor;
import org.buddycloud.channelserver.packetprocessor.message.event.SubscriptionProcessor;
import org.dom4j.Element;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

public class MessageProcessor implements PacketProcessor<Message> {

	private static final Logger logger = Logger
			.getLogger(MessageProcessor.class);

	private final BlockingQueue<Packet> outQueue;
	private ChannelManager channelManager;
	private Message message;
	private Properties configuration;

	public static final String ITEMS = "items";
	public static final String SUBSCRIPTION = "subscription";
	private static final Object AFFILIATION = "affiliation";

	public MessageProcessor(BlockingQueue<Packet> outQueue, Properties conf,
			ChannelManager channelManager) {
		this.outQueue = outQueue;
		this.channelManager = channelManager;
		this.configuration = conf;
	}

	@Override
	public void process(Message packet) throws Exception {

		message = packet;
		if (false == message.getType().equals(Message.Type.headline)) {
			return;
		}
		Element event = (Element) message.getElement().element("event")
				.elements().get(0);

		processEventContent(event.getName());
	}

	private void processEventContent(String name) throws Exception {
		
		logger.info("Processing event content type: '" + name + "'");
		PacketProcessor<Message> handler = null;
		if (name.equals(ITEMS)) {
			handler = new ItemsProcessor(outQueue, configuration,
					channelManager);
		} else if (name.equals(SUBSCRIPTION) || name.equals(AFFILIATION)) {
			handler = new SubscriptionProcessor(outQueue, configuration,
					channelManager);
		}
		if (null == handler) {
			throw new UnknownEventContentException("Unknown event content '"
					+ name + "'");
		}
		handler.process(message);
	}
}