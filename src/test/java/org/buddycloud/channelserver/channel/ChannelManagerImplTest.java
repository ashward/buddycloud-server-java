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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.buddycloud.channelserver.Configuration;
import org.buddycloud.channelserver.db.NodeStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.xmpp.packet.JID;

public class ChannelManagerImplTest {

	private static final String TEST_DOMAIN = "domain.com";
	
	@Mock
	NodeStore nodeStore;
	
	@Mock
	Configuration configuration;
	
	/**
	 * Class under test
	 */
	ChannelManagerImpl channelManager;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		channelManager = new ChannelManagerImpl(nodeStore, configuration);
		
		// This is used loads
		when(configuration.getProperty(Configuration.CONFIGURATION_SERVER_DOMAIN)).thenReturn(TEST_DOMAIN);
	}

	@After
	public void tearDown() throws Exception {
		channelManager = null;
	}

	@Test
	public void testCreatePersonalChannel() throws Exception {
		JID channelJID = new JID("testchannel@domain.com");
		
		channelManager.createPersonalChannel(channelJID);

		verify(nodeStore).createNode(channelJID, Conf.getPostChannelNodename(channelJID), Conf.getDefaultPostChannelConf(channelJID));
		verify(nodeStore).createNode(channelJID, Conf.getStatusChannelNodename(channelJID), Conf.getDefaultStatusChannelConf(channelJID));
		verify(nodeStore).createNode(channelJID, Conf.getGeoPreviousChannelNodename(channelJID), Conf.getDefaultGeoPreviousChannelConf(channelJID));
		verify(nodeStore).createNode(channelJID, Conf.getGeoCurrentChannelNodename(channelJID), Conf.getDefaultGeoCurrentChannelConf(channelJID));
		verify(nodeStore).createNode(channelJID, Conf.getGeoNextChannelNodename(channelJID), Conf.getDefaultGeoNextChannelConf(channelJID));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testCreatePersonalChannelFailsForRemoteUser() throws Exception {
		JID channelJID = new JID("testchannel@otherdomain.com");
		
		channelManager.createPersonalChannel(channelJID);
	}

	@Test
	public void testCreatePersonalChannelSomeNodesExist() throws Exception {
		JID channelJID = new JID("testchannel@domain.com");
		
		when(nodeStore.nodeExists(Conf.getPostChannelNodename(channelJID))).thenReturn(true);
		
		channelManager.createPersonalChannel(new JID("testchannel@domain.com"));

		verify(nodeStore, never()).createNode(channelJID, Conf.getPostChannelNodename(channelJID), Conf.getDefaultPostChannelConf(channelJID));
		verify(nodeStore).createNode(channelJID, Conf.getStatusChannelNodename(channelJID), Conf.getDefaultStatusChannelConf(channelJID));
		verify(nodeStore).createNode(channelJID, Conf.getGeoPreviousChannelNodename(channelJID), Conf.getDefaultGeoPreviousChannelConf(channelJID));
		verify(nodeStore).createNode(channelJID, Conf.getGeoCurrentChannelNodename(channelJID), Conf.getDefaultGeoCurrentChannelConf(channelJID));
		verify(nodeStore).createNode(channelJID, Conf.getGeoNextChannelNodename(channelJID), Conf.getDefaultGeoNextChannelConf(channelJID));
	}
	
	@Test
	public void testIsLocalNodeSuccess() throws Exception {
		assertTrue(channelManager.isLocalNode("/user/test@domain.com/posts"));
	}
	
	@Test
	public void testIsLocalNodeFailure() throws Exception {
		assertFalse(channelManager.isLocalNode("/user/test@otherdomain.com/posts"));		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIsLocalNodeWithInvalidNodeThrowsException() throws Exception {
		channelManager.isLocalNode("somerandomnodeid");		
	}
	
	@Test
	public void testIsLocalJID() throws Exception {
		
	}
}
