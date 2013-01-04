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

package org.buddycloud.channelserver;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Configuration extends Properties
{
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(Configuration.class);

	public static final String CONFIGURATION_SERVER_DOMAIN = "server.domain";
	public static final String CONFIGURATION_SERVER_CHANNELS_DOMAIN = "server.domain.channels";
	public static final String CONFIGURATION_SERVER_TOPICS_DOMAIN = "server.domain.topics";

	public static final String CONFIGURATION_XMPP_STANZA_MAXSIZE = "xmpp.stanza.max-size";

	private static final String CONFIGURATION_FILE = "configuration.properties";
	private static Configuration instance          = null;
	
    private Configuration()
    {
    	try {
	        load(new FileInputStream(CONFIGURATION_FILE));
    	} catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
    	}
    }
    
    public static Configuration getInstance() 
    {
    	if (null == instance) {
    		instance = new Configuration();
    	}
    	return instance;
    }
    
    public Integer getIntegerProperty(final String key) {
    	try {
    		return Integer.valueOf(getProperty(key));
    	} catch(NumberFormatException eNF) {
    		LOGGER.error("Expected integer for " + key + ", found " + getProperty(key), eNF);
    		return null;
    	}
    }

    public Integer getIntegerProperty(final String key, final Integer defaultValue) {
    	try {
    		String value = getProperty(key);
    		if(value != null) {
    			return Integer.valueOf(getProperty(key));
    		}
    	} catch(NumberFormatException eNF) {
    		LOGGER.error("Expected integer for " + key + ", found " + getProperty(key), eNF);
    	}
		return defaultValue;
    }
}