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

import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.packetprocessor.PacketProcessor;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.AbstractNamespace;
import org.buddycloud.channelserver.queue.FederatedQueueManager;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Packet;

public class JabberPubsub extends AbstractNamespace {

	public static final String NAMESPACE_URI    = "http://jabber.org/protocol/pubsub";

	public static final String NS_XMPP_STANZAS  = "urn:ietf:params:xml:ns:xmpp-stanzas";
	public static final String NS_PUBSUB_ERROR  = "http://jabber.org/protocol/pubsub#errors";
	public static final String NS_PUBSUB_EVENT  = "http://jabber.org/protocol/pubsub#event";
	public static final String NS_PUBSUB_OWNER  = "http://jabber.org/protocol/pubsub#owner";
	public static final String NS_DISCO_ITEMS   = "http://jabber.org/protocol/disco#items";
	public static final String NS_DISCO_INFO   = "http://jabber.org/protocol/disco#info";
	public static final String NS_AUTHORIZATION = "http://jabber.org/protocol/pubsub#subscribe_authorization";

	public static final String VAR_NODE           = "pubsub#node";
	public static final String VAR_SUBSCRIBER_JID = "pubsub#subscriber_jid";
	public static final String VAR_ALLOW          = "pubsub#allow";

	public static final String NS_BUDDYCLOUD = "http://buddycloud.org/v1";

	private final PubSubGet getProcessor;
	private final PubSubSet setProcessor;
	private final PubSubResult resultProcessor;

	public JabberPubsub(BlockingQueue<Packet> outQueue, Properties conf,
			ChannelManager channelManager, FederatedQueueManager federatedQueueManager) {

		super(outQueue, conf, channelManager);
		this.getProcessor = new PubSubGet(outQueue, channelManager);
		this.setProcessor = new PubSubSet(outQueue, channelManager);
		this.resultProcessor = new PubSubResult(outQueue, federatedQueueManager);
	}

	@Override
	protected PacketProcessor<IQ> get() {
		return getProcessor;
	}

	@Override
	protected PacketProcessor<IQ> set() {
		return setProcessor;
	}

	@Override
	protected PacketProcessor<IQ> result() {
		return resultProcessor;
	}

	@Override
	protected PacketProcessor<IQ> error() {
		return null;
	}
}