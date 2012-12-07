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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmpp.packet.JID;

public class ChannelNodeRef {
	private static Pattern pattern = Pattern.compile("^/user/([^/]+)/?(.*)$");
	
	private JID jid;
	private String type;
	
	public ChannelNodeRef(final JID jid, final String type) {
		this.jid = jid;
		this.type = type;
	}
	
	/**
	 * Returns the jid portion of the node id.
	 * @return
	 */
	public JID getJID() {
		return jid;
	}
	
	/**
	 * Returns the type of the node (e.g. "posts", "subscriptions", etc).
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Converts the node ref into a node id string.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("/user/");
		sb.append(jid.toBareJID());
		
		if(type != null) {
			sb.append("/");
			sb.append(type);
		}
		
		return sb.toString();
	}
	
	/**
	 * Parses a node id into a channel node ref.
	 * @param nodeId the node id string.
	 * @return the channel ref.
	 * @throws IllegalArgumentException if the node id does not represent a valid channels protocol node.
	 */
	public static ChannelNodeRef fromNodeId(final String nodeId) {
		Matcher m = pattern.matcher(nodeId);
		
		if(m.matches()) {
			return new ChannelNodeRef(new JID(m.group(1)), m.group(2));
		}
		
		throw new IllegalArgumentException(nodeId + " does represent a valid channel node id");
	}
}
