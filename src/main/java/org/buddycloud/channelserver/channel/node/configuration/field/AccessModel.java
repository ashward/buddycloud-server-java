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

public class AccessModel extends Field
{
	public static final String FIELD_NAME    = "pubsub#access_model";
	public static final String DEFAULT_VALUE = AccessModel.models.OPEN.toString();
	
	public enum models { 
		OPEN("open"), WHITELIST("whitelist"), AUTHORIZE("authorize");
		String model = null;
		private models(String model) {
			this.model = model;
		}
		public String toString() {
	        return model;
	    }
	}

	public AccessModel()
	{
		name = FIELD_NAME;
	}
	
	public boolean isValid()
	{
		return (getValue().equals(AccessModel.models.OPEN.toString())
			|| getValue().equals(AccessModel.models.WHITELIST.toString())
			|| getValue().equals(AccessModel.models.AUTHORIZE.toString())
		);
	}
}
