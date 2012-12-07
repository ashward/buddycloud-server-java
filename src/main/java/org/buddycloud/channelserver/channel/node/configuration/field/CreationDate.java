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

import java.util.Date;
import java.text.SimpleDateFormat;

public class CreationDate extends Field
{
	public static final String FIELD_NAME    = "pubsub#creation_date";
	public static final String DEFAULT_VALUE = "1955-11-05T01:21:00Z";

	public static SimpleDateFormat ISO8601FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	public CreationDate()
	{
		setValue(ISO8601FORMAT.format(new Date()));
		name = FIELD_NAME;
	}

	public boolean isValid()
	{
		// @todo improve this validation later
		return getValue().matches("/^[0-9]{4}-[0-9}{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}Z$/");
	}
}
