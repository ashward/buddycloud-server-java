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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.packetprocessor.PacketProcessor;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.get.AffiliationsGet;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.get.ItemsGet;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.get.SubscriptionsGet;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;

public class PubSubGet implements PacketProcessor<IQ> {

	public static final String ELEMENT_NAME = "pubsub";

	private final BlockingQueue<Packet> outQueue;
	private final ChannelManager channelManager;
	private final List<PubSubElementProcessor> elementProcessors = new LinkedList<PubSubElementProcessor>();

	public PubSubGet(BlockingQueue<Packet> outQueue,
			ChannelManager channelManager) {
		this.outQueue = outQueue;
		this.channelManager = channelManager;
		initElementProcessors();
	}

	private void initElementProcessors() {
		elementProcessors.add(new SubscriptionsGet(outQueue, channelManager));
		elementProcessors.add(new AffiliationsGet(outQueue, channelManager));
		elementProcessors.add(new ItemsGet(outQueue, channelManager));
	}

	@Override
	public void process(IQ reqIQ) throws Exception {

		Element pubsub = reqIQ.getChildElement();

		JID actorJID = null;
		if (pubsub.element("actor") != null) {
			actorJID = new JID(pubsub.element("actor").getTextTrim());
		}

		// Let's get the possible rsm element
		Element rsm = pubsub.element(new QName("set", new Namespace("",
				"http://jabber.org/protocol/rsm")));

		@SuppressWarnings("unchecked")
		List<Element> elements = pubsub.elements();

		boolean handled = false;
		for (Element x : elements) {
			for (PubSubElementProcessor elementProcessor : elementProcessors) {
				if (elementProcessor.accept(x)) {
					elementProcessor.process(x, actorJID, reqIQ, rsm);
					handled = true;
				}
			}
		}

		if (handled == false) {

			// <iq type='error'
			// from='pubsub.shakespeare.lit'
			// to='hamlet@denmark.lit/elsinore'
			// id='create1'>
			// <error type='cancel'>
			// <feature-not-implemented
			// xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>
			// <unsupported xmlns='http://jabber.org/protocol/pubsub#errors'
			// feature='create-nodes'/>
			// </error>
			// </iq>

			// TODO, fix this. Now we just reply unexpected_request.
			// We should answer something like above.

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

}
