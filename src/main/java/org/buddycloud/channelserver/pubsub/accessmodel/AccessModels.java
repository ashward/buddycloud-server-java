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

package org.buddycloud.channelserver.pubsub.accessmodel;

public enum AccessModels {

	authorize, open, presence, roster, whitelist;

	public static AccessModels createFromString(String asString) {

		if ("authorize".equals(asString)) {
			return authorize;
		} else if ("open".equals(asString)) {
			return open;
		} else if ("presence".equals(asString)) {
			return presence;
		} else if ("roster".equals(asString)) {
			return roster;
		} else if ("whitelist".equals(asString)) {
			return whitelist;
		}
		return whitelist;
	}

}
