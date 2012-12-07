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

package org.buddycloud.channelserver.channel.node.configuration.field;

public class Affiliation extends Field {
	public static final String FIELD_NAME = "buddycloud#default_affiliation";
	public static final String DEFAULT_VALUE = Affiliation.models.MEMBER
			.toString();

	public enum models {
		FOLLOWER_AND_POST("publisher"), MEMBER("member"), OWNER("owner"), MODERATOR(
				"moderator");
		String model = null;

		private models(String model) {
			this.model = model;
		}

		public String toString() {
			return model;
		}
	}

	public Affiliation() {
		name = FIELD_NAME;
	}

	public boolean isValid() {
		return (getValue().equals(
				Affiliation.models.FOLLOWER_AND_POST.toString()) 
		    || getValue().equals(Affiliation.models.MEMBER.toString())
		    || getValue().equals(Affiliation.models.OWNER.toString())
		    || getValue().equals(Affiliation.models.MODERATOR.toString())
	    );
	}
}
