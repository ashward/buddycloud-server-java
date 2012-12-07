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

package org.buddycloud.channelserver.channel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.xmpp.packet.JID;

public class ChannelNodeRefTest {

	@Test
	public void testToString() {
		JID jid = new JID("user@thing.com");
		String type = "posts";
		
		String expected = "/user/user@thing.com/posts";
		
		ChannelNodeRef ref = new ChannelNodeRef(jid, type);
		
		assertEquals(expected, ref.toString());
	}

	@Test
	public void testFromNodeId() {
		String nodeId = "/user/user@thing.com/posts";

		JID jid = new JID("user@thing.com");
		String type = "posts";
		
		ChannelNodeRef ref = ChannelNodeRef.fromNodeId(nodeId);
		
		assertEquals("Unexpected JID", jid, ref.getJID());
		assertEquals("Unexpected type", type, ref.getType());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testFromNodeIdWithInvalidId() {
		String nodeId = "some random invalid id";

		ChannelNodeRef.fromNodeId(nodeId);
	}

}
