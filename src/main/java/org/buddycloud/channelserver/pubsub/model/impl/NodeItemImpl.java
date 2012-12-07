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

package org.buddycloud.channelserver.pubsub.model.impl;

import java.util.Date;

import org.buddycloud.channelserver.pubsub.model.NodeItem;

/**
 * A basic immutable implementation of {@link NodeItem}.
 */
public class NodeItemImpl implements NodeItem {

	private final String nodeId;
	private final String id;
	private final String payload;
	private final Date updated;

	/**
	 * Create a new NodeItemImpl with the given data.
	 * 
	 * @param nodeId
	 *            The node id.
	 * @param id
	 *            The item id
	 * @param updated
	 *            The item's update timestamp.
	 * @param payload
	 *            The item's payload.
	 */
	public NodeItemImpl(final String nodeId, final String id,
			final Date updated, final String payload) {
		this.nodeId = nodeId;
		this.id = id;
		this.updated = updated;
		this.payload = payload;
	}

	@Override
	public String getNodeId() {
		return nodeId;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getPayload() {
		return payload;
	}

	@Override
	public Date getUpdated() {
		return updated;
	}

	@Override
	public String getUID() {
		return id;
	}
}
