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

package org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.buddycloud.channelserver.packetprocessor.PacketProcessor;
import org.buddycloud.channelserver.queue.FederatedQueueManager;
import org.buddycloud.channelserver.queue.UnknownFederatedPacketException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;

public class PubSubResult implements PacketProcessor<IQ> {

	public static final String ELEMENT_NAME = "pubsub";
	private Logger logger = Logger.getLogger(PubSubResult.class);
	private final BlockingQueue<Packet> outQueue;
	private FederatedQueueManager federatedQueueManager;

	public PubSubResult(BlockingQueue<Packet> outQueue,
			FederatedQueueManager federatedQueueManager) {
		this.outQueue = outQueue;
		this.federatedQueueManager = federatedQueueManager;
	}

	@Override
	public void process(IQ reqIQ) throws Exception {
		try {
			federatedQueueManager.passResponseToRequester(reqIQ);
			return;
		} catch (UnknownFederatedPacketException e) {
			logger.error(e);
		}

		IQ reply = IQ.createResultIQ(reqIQ);
		reply.setChildElement(reqIQ.getChildElement().createCopy());
		reply.setType(Type.error);
		PacketError pe = new PacketError(
				org.xmpp.packet.PacketError.Condition.unexpected_request,
				org.xmpp.packet.PacketError.Type.wait);
		reply.setError(pe);
		outQueue.put(reply);
	}
}
