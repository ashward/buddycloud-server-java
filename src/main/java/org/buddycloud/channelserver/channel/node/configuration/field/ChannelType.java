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

public class ChannelType extends Field
{
	public static final String FIELD_NAME    = "buddycloud#channel_type";
	public static final String DEFAULT_VALUE = ChannelType.models.PERSONAL.toString();
	
	public ChannelType()
	{
		name = FIELD_NAME;
	}
	
	public enum models { 
		PERSONAL("personal"), TOPIC("topic");
		String model = null;
		private models(String model) {
			this.model = model;
		}
		public String toString() {
	        return model;
	    }
	}

	public boolean isValid()
	{
		return (getValue().equals(ChannelType.models.PERSONAL)
			|| getValue().equals(ChannelType.models.TOPIC)
		);
	}
}
