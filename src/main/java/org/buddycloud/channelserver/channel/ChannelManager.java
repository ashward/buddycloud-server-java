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

import org.buddycloud.channelserver.db.NodeStore;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.xmpp.packet.JID;

public interface ChannelManager extends NodeStore {
	
	/**
	 * Creates a channel.
	 * @param channelJID the JID of the channel.
	 * @throws NodeStoreException 
	 */
	void createPersonalChannel(JID ownerJID) throws NodeStoreException;
	
	/**
	 * Determines whether the node id given refers to a local node.
	 * @param nodeId the node id
	 * @return <code>true</code> if the node appears to be local, <code>false</code> otherwise.
	 * @throws NodeStoreException 
	 */
	boolean isLocalNode(String nodeId) throws NodeStoreException;
	
	/**
	 * Determines whether the jid refers to a local user.
	 * @param jid the user's jid
	 * @return <code>true</code> if the jid appears to be local, <code>false</code> otherwise.
	 * @throws NodeStoreException 
	 */
	boolean isLocalJID(JID jid) throws NodeStoreException;
	
	/**
	 * Returns the maximum size, in bytes, that a stanza should be.
	 * @return the size in bytes.
	 */
	int getMaximumStanzaSize();
}
