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

package org.buddycloud.channelserver.pubsub.affiliation;

public enum Affiliations {

	owner, moderator, publisher, member, none, outcast;

	public static Affiliations createFromString(String asString) {

		if ("owner".equals(asString)) {
			return owner;
		} else if ("publisher".equals(asString)) {
			return publisher;
		} else if ("moderator".equals(asString)) {
			return moderator;
		} else if ("member".equals(asString)) {
			return member;
		} else if ("none".equals(asString)) {
			return none;
		} else if ("outcast".equals(asString)) {
			return outcast;
		}
		return none;
	}
	
	public boolean in(Affiliations... affiliations) {
		for(Affiliations a : affiliations) {
			if(a.equals(this)) {
				return true;
			}
		}
		
		return false;
		
	}

	public boolean canAuthorize() {
		return in(owner, moderator);
	}
}
