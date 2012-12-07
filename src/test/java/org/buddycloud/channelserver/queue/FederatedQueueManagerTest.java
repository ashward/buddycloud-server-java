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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.Assert;

import org.buddycloud.channelserver.ChannelsEngine;
import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.packetHandler.iq.IQTestHandler;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.JabberPubsub;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;
import org.buddycloud.channelserver.channel.ChannelsEngineMock;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

public class FederatedQueueManagerTest extends IQTestHandler {

	private FederatedQueueManager queueManager;
	private BlockingQueue<Packet> queue = new LinkedBlockingQueue<Packet>();
	private ChannelManager channelManager;
	private ChannelsEngineMock channelsEngine;
	private String localServer = "channels.shakespeare.lit";

	@Before
	public void setUp() throws Exception {
		channelsEngine = new ChannelsEngineMock();
		channelManager = Mockito.mock(ChannelManager.class);

		queueManager = new FederatedQueueManager(channelsEngine, localServer);
	}

	@Test(expected = UnknownFederatedPacketException.class)
	public void testAttemptingToForwardNonExistantFederatedPacketThrowsException()
			throws Exception {
		IQ packet = new IQ();
		packet.setID("1");
		queueManager.passResponseToRequester(packet);
	}

	@Test
	public void testUnDiscoveredServerSpawnsDiscoItemsRequest()
			throws Exception {
		IQ packet = new IQ();
		packet.setID("1");
		packet.setFrom(new JID("romeo@montague.lit/street"));
		packet.setTo(new JID("capulet.lit"));
		packet.setType(IQ.Type.get);

		queueManager.process(packet);

		Assert.assertEquals(1, channelsEngine.size());
		Packet outgoingPacket = channelsEngine.poll();

		Assert.assertEquals(localServer, outgoingPacket.getElement()
				.attributeValue("from"));
		Assert.assertEquals(JabberPubsub.NS_DISCO_ITEMS, outgoingPacket
				.getElement().element("query").getNamespace().getStringValue());
	}

	@Test
	public void testResponseToDiscoItemsRequestResultsInDiscoInfoRequests()
			throws Exception {

		Element item = new DefaultElement("item");
		item.addAttribute("jid", "channels.capulet.lit");
		ArrayList<Element> items = new ArrayList<Element>();
		items.add(item);

		queueManager.sendInfoRequests(new JID("capulet.lit"), items);

		Assert.assertEquals(1, channelsEngine.size());
		Packet discoInfoRequest = channelsEngine.poll();

		Assert.assertEquals(localServer, discoInfoRequest.getElement()
				.attributeValue("from"));
		Assert.assertEquals("channels.capulet.lit", discoInfoRequest
				.getElement().attributeValue("to"));
		Assert.assertEquals(JabberPubsub.NS_DISCO_INFO, discoInfoRequest
				.getElement().element("query").getNamespace().getStringValue());
	}

	@Test
	public void testNotProvidingChannelServerInItemsResponseResultsInErrorStanazaReturn()
			throws Exception {

		// Attempt to send stanza
		IQ packet = new IQ();
		packet.setID("1:some-request");
		packet.setFrom(new JID("romeo@montague.lit/street"));
		packet.setTo(new JID("capulet.lit"));
		packet.setType(IQ.Type.get);

		queueManager.process(packet);

		// Disco#items fired, passing in items list
		Element item = new DefaultElement("item");
		item.addAttribute("jid", "channels.capulet.lit");
		ArrayList<Element> items = new ArrayList<Element>();
		items.add(item);
		queueManager.sendInfoRequests(new JID("capulet.lit"), items);
		channelsEngine.poll();

		// Response to disco#info with no identities
		queueManager.processInfoResponses(new JID("channels.capulter.lit"),
				channelsEngine.poll().getID(), new ArrayList<Element>());

		// Expect error response to original packet
		IQ errorResponse = (IQ) channelsEngine.poll();
		Assert.assertEquals(packet.getID(), errorResponse.getID());
		Assert.assertEquals(IQ.Type.error, errorResponse.getType());
		Assert.assertEquals(localServer, errorResponse.getFrom().toString());

		Assert.assertEquals(PacketError.Type.cancel.toXMPP(), errorResponse
				.getElement().element("error").attributeValue("type"));
		Assert.assertEquals(
				PacketError.Condition.item_not_found.toXMPP(),
				errorResponse.getElement().element("error")
						.element("item-not-found").getName());
		Assert.assertEquals(
				"No pubsub channel service discovered for capulet.lit",
				errorResponse.getElement().element("error").elementText("text"));
	}

	@Test
	public void testPassingChannelServerIdentifierViaItemsResultsInQueuedPacketSending()
			throws Exception {
		// Attempt to send stanza
		IQ packet = new IQ();
		packet.setID("1:some-request");
		packet.setFrom(new JID("romeo@montague.lit/street"));
		packet.setTo(new JID("topics.capulet.lit"));
		packet.setType(IQ.Type.get);

		queueManager.process(packet);
        channelsEngine.poll();
        
		// Pass in items with name="buddycloud-server"
		Element item = new DefaultElement("item");
		item.addAttribute("jid", "channels.capulet.lit");
		item.addAttribute("name", "buddycloud-server");
		ArrayList<Element> items = new ArrayList<Element>();
		items.add(item);
		queueManager.sendInfoRequests(new JID("topics.capulet.lit"), items);

		// Note original packet now sent with remote channel server tag
		Packet originalPacketRedirected = channelsEngine.poll();

		Packet expectedForwaredPacket = packet.createCopy();
		expectedForwaredPacket.setFrom(new JID(localServer));
		expectedForwaredPacket.setTo(new JID("channels.capulet.lit"));
		Assert.assertEquals(expectedForwaredPacket.toXML(),
				originalPacketRedirected.toXML());
	}
}