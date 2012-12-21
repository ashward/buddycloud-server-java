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

package org.buddycloud.channelserver.exception;

import org.buddycloud.channelserver.XmppException;
import org.xmpp.packet.PacketError;

/**
 * Represents a defined XMPP error has occurred. The given details will be returned to the originator
 * if possible as an error stanza.
 */
public class GenericXmppException extends XmppException {

	private final PacketError error;
	
	private static final long serialVersionUID = 1L;

	public GenericXmppException(final PacketError.Type type, final PacketError.Condition condition) {
		super();
		error = new PacketError(condition, type);
	}

	public GenericXmppException(final PacketError.Type type, final PacketError.Condition condition, Throwable t) {
		super(t);
		error = new PacketError(condition, type);
	}
	
	public PacketError getPacketError() {
		return error;
	}
}
