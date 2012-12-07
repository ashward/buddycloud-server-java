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

package org.buddycloud.channelserver.packetprocessor.iq.namespace.register;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;


import org.buddycloud.channelserver.packetprocessor.PacketProcessor;
import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.AbstractNamespace;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Packet;

public final class JabberRegister extends AbstractNamespace {

	public static final String NAMESPACE_URI = "jabber:iq:register";
	
	private final PacketProcessor<IQ> setProcessor;
	
	public JabberRegister(BlockingQueue<Packet> outQueue, Properties conf, ChannelManager channelManager) {
		super(outQueue, conf, channelManager);
		this.setProcessor = new RegisterSet(conf, outQueue, channelManager);
	}

    @Override
    protected PacketProcessor<IQ> get() {
        return null;
    }

    @Override
    protected PacketProcessor<IQ> set() {
        return setProcessor;
    }

    @Override
    protected PacketProcessor<IQ> result() {
        return null;
    }

    @Override
    protected PacketProcessor<IQ> error() {
        return null;
    }
}
