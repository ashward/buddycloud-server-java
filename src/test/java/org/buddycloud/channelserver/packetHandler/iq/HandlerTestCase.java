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

package org.buddycloud.channelserver.packetHandler.iq;

import java.util.concurrent.LinkedBlockingQueue;

import org.buddycloud.channelserver.Configuration;
import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.channel.TestHelper;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.xmpp.packet.Packet;

public class HandlerTestCase {
	protected TestHelper helper;
	
	@Mock
	protected Configuration config;
	
	protected LinkedBlockingQueue<Packet> outQueue;
	protected LinkedBlockingQueue<Packet> inQueue;
    
    /**
     *  A channel manager which can be used to initialise nodes, etc. for tests.
     */
	protected ChannelManager channelManager;

    @Before
	public void setUp() throws Exception {
    	MockitoAnnotations.initMocks(this);

    	helper = new TestHelper();
    	
        this.outQueue = helper.getOutQueue();
        this.inQueue = helper.getInQueue();
        
        channelManager = helper.getChannelManagerFactory().create();
	}
}
