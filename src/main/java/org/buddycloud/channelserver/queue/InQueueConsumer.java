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

package org.buddycloud.channelserver.queue;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.channel.ChannelManagerFactory;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.packetprocessor.iq.IQProcessor;
import org.buddycloud.channelserver.packetprocessor.message.MessageProcessor;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

public class InQueueConsumer extends QueueConsumer {

	private static final Logger logger = Logger
			.getLogger(InQueueConsumer.class);

	private final BlockingQueue<Packet> outQueue;
	private final Properties conf;
	private final ChannelManagerFactory channelManagerFactory;
	private final FederatedQueueManager federatedQueueManager;

	public InQueueConsumer(BlockingQueue<Packet> outQueue, Properties conf,
			BlockingQueue<Packet> inQueue,
			ChannelManagerFactory channelManagerFactory,
			FederatedQueueManager federatedQueueManager) {
		super(inQueue);
		this.outQueue = outQueue;
		this.conf = conf;
		this.channelManagerFactory = channelManagerFactory;
		this.federatedQueueManager = federatedQueueManager;
	}

	@Override
	protected void consume(Packet p) {
		ChannelManager channelManager = null;
		try {
			Long start = System.currentTimeMillis();

			String xml = p.toXML();
			logger.debug("Received payload: '" + xml + "'.");
			channelManager = channelManagerFactory.create();
			if (p instanceof IQ) {
				new IQProcessor(outQueue, conf, channelManager,
						federatedQueueManager).process((IQ) p);
			} else if (p instanceof Message) {
				new MessageProcessor(outQueue, conf, channelManager)
						.process((Message) p);
			} else {
				logger.info("Not handling following stanzas yet: '" + xml
						+ "'.");
			}

			logger.debug("Payload handled in '"
					+ Long.toString((System.currentTimeMillis() - start))
					+ "' milliseconds.");

		} catch (Exception e) {
			logger.debug("Exception: " + e.getMessage(), e);
		} finally {
			try {
				channelManager.close();
			} catch (NodeStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
