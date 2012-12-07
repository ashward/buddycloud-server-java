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

package org.buddycloud.channelserver.pubsub.model;

import org.buddycloud.channelserver.pubsub.affiliation.Affiliations;
import org.xmpp.packet.JID;
import org.xmpp.resultsetmanagement.Result;

/**
 * Represents a user's affiliation with a node
 */
public interface NodeAffiliation extends Result {
	/**
	 * Gets the user who is affiliated with the node
	 * 
	 * @return the JID
	 */
	JID getUser();

	/**
	 * Gets the node to which the user is affiliated
	 * 
	 * @return
	 */
	String getNodeId();

	/**
	 * The type of affiliation
	 * 
	 * @return
	 */
	Affiliations getAffiliation();
}
