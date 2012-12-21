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

import static org.mockito.Mockito.mock;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.buddycloud.channelserver.ChannelsEngine;
import org.buddycloud.channelserver.Configuration;
import org.xmpp.packet.Packet;

public class ChannelsEngineMock extends ChannelsEngine {

	private BlockingQueue<Packet> queue = new LinkedBlockingQueue<Packet>();
	
	public ChannelsEngineMock() {
		super(mock(Configuration.class));
	}

	public void sendPacket(Packet packet) {
		try {
		    queue.put(packet);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public Packet poll() {
		return queue.poll();
	}
	
	public int size() {
		return queue.size();
	}
}
