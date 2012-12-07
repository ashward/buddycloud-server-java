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

package org.buddycloud.channelserver.packetprocessor.iq.namespace;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.packetprocessor.PacketProcessor;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;

public abstract class AbstractNamespace implements PacketProcessor<IQ> {

	private static final Logger LOGGER = Logger
			.getLogger(AbstractNamespace.class);

	private BlockingQueue<Packet> outQueue;
	private Properties conf;
	private ChannelManager channelManager;

	public AbstractNamespace(BlockingQueue<Packet> outQueue, Properties conf,
			ChannelManager channelManager) {
		LOGGER.trace("In " + this.getClass().getName());
		this.outQueue = outQueue;
		this.conf = conf;
		this.channelManager = channelManager;
	}

	protected abstract PacketProcessor<IQ> get();

	protected abstract PacketProcessor<IQ> set();

	protected abstract PacketProcessor<IQ> result();

	protected abstract PacketProcessor<IQ> error();

	@Override
	public void process(IQ reqIQ) throws Exception {

		PacketProcessor<IQ> processor = null;

		switch (reqIQ.getType()) {
		case get:
			processor = get();
			break;
		case set:
			processor = set();
			break;
		case result:
			processor = result();
			break;
		case error:
			processor = error();
			break;
		default:
			break;
		}

		if (processor != null) {
			processor.process(reqIQ);
			return;
		}

		if (reqIQ.getType() == IQ.Type.error
				|| reqIQ.getType() == IQ.Type.result) {
//			handleStateReply(reqIQ);
		} else {
			handleUnexpectedRequest(reqIQ);
		}
	}

	private void handleUnexpectedRequest(IQ reqIQ) throws InterruptedException {
		IQ reply = IQ.createResultIQ(reqIQ);
		reply.setChildElement(reqIQ.getChildElement().createCopy());
		reply.setType(Type.error);
		PacketError pe = new PacketError(
				PacketError.Condition.unexpected_request, PacketError.Type.wait);
		reply.setError(pe);

		outQueue.put(reply);
	}

	protected ChannelManager getChannelManager() {
		return channelManager;
	}

	protected BlockingQueue<Packet> getOutQueue() {
		return outQueue;
	}

	public Properties getConf() {
		return conf;
	}
}
