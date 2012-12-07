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

public class Factory
{	
	public Field create(String type, String value)
    {
    	if ((null == type) || (null == value)) {
    		throw new ConfigurationFieldException();
    	}
    	if (type.equals("pubsub#owner")) {
    		throw new ConfigurationFieldException();
    	} else if (type.equals(ChannelTitle.FIELD_NAME)) {
    	    ChannelTitle field = new ChannelTitle();
    	    field.setValue(value);
    	    return field;
    	} else if (type.equals(ChannelDescription.FIELD_NAME)) {
    		ChannelDescription field = new ChannelDescription();
    		field.setValue(value);
    		return field;
    	} else if (type.equals(AccessModel.FIELD_NAME)) {
    		AccessModel field = new AccessModel();
    		field.setValue(value);
    		return field;
    	} else if (type.equals(Affiliation.FIELD_NAME)) {
    		Affiliation field = new Affiliation();
    		field.setValue(value);
    		return field;
    	} else if (type.equals(CreationDate.FIELD_NAME)) {
    		CreationDate field = new CreationDate();
    		field.setValue(value);
    		return field;
    	} else if (type.equals(ChannelType.FIELD_NAME)) {
    		ChannelType field = new ChannelType();
    	    field.setValue(value);
    	    return field;
    	} else {
    		Generic field = new Generic();
    	    field.setName(type);
    	    field.setValue(value);
    	    return field;
    	}
    }
}