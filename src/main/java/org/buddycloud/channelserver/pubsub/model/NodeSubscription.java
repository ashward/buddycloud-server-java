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

import org.buddycloud.channelserver.pubsub.subscription.Subscriptions;
import org.xmpp.packet.JID;
import org.xmpp.resultsetmanagement.Result;

/**
 * Represents a user's subscription to a node
 */
public interface NodeSubscription extends Result {

	/**
	 * Gets the subscription type
	 * @return
	 */
    Subscriptions getSubscription();
    
    /**
     * Gets the user
     * @return
     */
    JID getUser();
    
    /**
     * Gets the listener for notifications (for the inbox protocol)
     * @return the listener's JID, or <code>null</code> if not a remote subscription.
     */
    JID getListener();
    
    /**
     * Gets the node id
     * @return
     */
    String getNodeId();
}
