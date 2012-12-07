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

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.buddycloud.channelserver.ChannelsEngine;
import org.xmpp.component.ComponentException;
import org.xmpp.packet.Packet;

public class OutQueueConsumer extends QueueConsumer {

    private static final Logger logger = Logger.getLogger(OutQueueConsumer.class);
    private final ChannelsEngine component;
	private FederatedQueueManager federatedQueue;
	private String server;
     
    public OutQueueConsumer(ChannelsEngine component, 
            BlockingQueue<Packet> outQueue, FederatedQueueManager federatedQueue, String server) {
        super(outQueue);
        this.component = component;
        this.federatedQueue = federatedQueue;
        this.server = server;
    }

	@Override
    protected void consume(Packet p) {
        try {
        	if ((-1 == p.getTo().toString().indexOf("@")) 
        	    && (p.getTo().toBareJID().indexOf(server) == -1)) {
        		// i.e. a remote server
        		federatedQueue.process(p);
        		return;
        	}
            component.sendPacket(p);
            logger.debug("OUT -> " + p.toXML());
        } catch (ComponentException e) {
            logger.error(e);
        }
    }
}