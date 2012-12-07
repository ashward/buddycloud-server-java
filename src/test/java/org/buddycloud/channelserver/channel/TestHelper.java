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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;

import org.buddycloud.channelserver.db.NodeStore;
import org.buddycloud.channelserver.db.NodeStoreFactory;
import org.buddycloud.channelserver.db.jdbc.DatabaseTester;
import org.buddycloud.channelserver.db.jdbc.JDBCNodeStore;
import org.buddycloud.channelserver.db.jdbc.dialect.Sql92NodeStoreDialect;
import org.buddycloud.channelserver.packetHandler.iq.IQTestHandler;
import org.buddycloud.channelserver.queue.InQueueConsumer;
import org.xmpp.packet.Packet;

public class TestHelper {
	LinkedBlockingQueue<Packet> outQueue;
	LinkedBlockingQueue<Packet> inQueue;
	InQueueConsumer consumer;
	
	ChannelManagerFactory channelManagerFactory;
	
	public TestHelper() throws FileNotFoundException, IOException {
		initialiseChannelManagerFactory();
		
        outQueue = new LinkedBlockingQueue<Packet>();
        inQueue = new LinkedBlockingQueue<Packet>();
        consumer = new InQueueConsumer(outQueue, IQTestHandler.readConf(), inQueue, channelManagerFactory, null);
        consumer.start();
	}
	
	public LinkedBlockingQueue<Packet> getOutQueue() {
		return outQueue;
	}
	
	public LinkedBlockingQueue<Packet> getInQueue() {
		return inQueue;
	}
	
	public InQueueConsumer getConsumer() {
		return consumer;
	}
	  
	public ChannelManagerFactory getChannelManagerFactory() {
		return channelManagerFactory;
	}
	
    private ChannelManagerFactory initialiseChannelManagerFactory() {
    	NodeStoreFactory nsFactory = new NodeStoreFactory() {
			
			@Override
			public NodeStore create() {
					try {
						return new JDBCNodeStore(new DatabaseTester().getConnection(), new Sql92NodeStoreDialect());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
			}
		};
    	
		try {
			channelManagerFactory = new ChannelManagerFactoryImpl(IQTestHandler.readConf(), nsFactory);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
	
}
