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
import java.util.concurrent.LinkedBlockingQueue;
import junit.framework.Assert;
import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.packetHandler.iq.IQTestHandler;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.JabberPubsub;
import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

public class MessageProcessorTest extends IQTestHandler {
	private Message message;
	private MessageProcessor messageProcessor;

	private BlockingQueue<Packet> queue = new LinkedBlockingQueue<Packet>();

	private ChannelManager channelManager;

	@Before
	public void setUp() throws Exception {

		Properties conf = new Properties();
		channelManager = Mockito.mock(ChannelManager.class);
		messageProcessor = new MessageProcessor(queue, conf, channelManager);
	}
	
	@Test
	public void testNonHeadlineEventPerformsNoAction() throws Exception {
		Message message = new Message();
		message.setType(Message.Type.chat);
		
		messageProcessor.process(message);
		Assert.assertEquals(0, queue.size());
	}

	@Test(expected=NullPointerException.class)
	public void testNoEventElementPresentThrowsException() throws Exception {
		Message message = new Message();
		message.setType(Message.Type.headline);
		
		messageProcessor.process(message);
	}
	
	@Test(expected=UnknownEventContentException.class)
	public void testUnknownEventContentTypeThrowsException() throws Exception {
		Message message = new Message();
		message.setType(Message.Type.headline);
		Element event = message.addChildElement("event", JabberPubsub.NS_PUBSUB_EVENT);
		event.addElement("random");
		
		messageProcessor.process(message);
	}
}