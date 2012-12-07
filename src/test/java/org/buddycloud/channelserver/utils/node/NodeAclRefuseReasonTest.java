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

package org.buddycloud.channelserver.utils.node;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xmpp.packet.PacketError;
import org.xmpp.packet.PacketError.Condition;
import org.xmpp.packet.PacketError.Type;

public class NodeAclRefuseReasonTest extends TestCase {

	Type type = PacketError.Type.cancel;
	Condition condition = PacketError.Condition.feature_not_implemented;
	String additionalErrorElement;

	NodeAclRefuseReason reason;

	@Before
	public void setUp() {
		reason = new NodeAclRefuseReason(type, condition,
				additionalErrorElement);
	}

	@Test
	public void testCallingGetTypeReturnsExpectedErrorType() {
		assertEquals(type.toString(), reason.getType().toString());
	}

	@Test
	public void testCallingGetConditionReturnsExpectedErrorCondition() {
		assertEquals(condition.toString(), reason.getCondition().toString());
	}

	@Test
	public void testCallingGetAdditionalErrorElementWithoutSettingReturnsNull() {
		assertNull(reason.getAdditionalErrorElement());
	}

	@Test
	public void testPassingAnAdditionErrorElementRetunsAsExpected() {
		reason = new NodeAclRefuseReason(type, condition, "nodeid-required");
		assertEquals("nodeid-required", reason.getAdditionalErrorElement());
	}
}
