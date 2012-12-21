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

import org.apache.log4j.Logger;
import org.jivesoftware.whack.ExternalComponentManager;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.configuration.PropertyConfigurator;
import org.xmpp.component.ComponentException;

public class XmppComponent {

	private static final String DATABASE_CONFIGURATION_FILE = "db.properties";
	
	private static final Logger LOGGER = Logger.getLogger(XmppComponent.class);
	private String hostname;
	private int socket;
	
	private String domainName;
	private String password;
	private Configuration conf;
	
	public XmppComponent(Configuration conf) {
	    setConf(conf);
		hostname = conf.getProperty("xmpp.host");
		socket = Integer.valueOf(conf.getProperty("xmpp.port"));
		domainName = conf.getProperty("xmpp.subdomain");
		password = conf.getProperty("xmpp.secretkey");

		try {
			PropertyConfigurator.configure(DATABASE_CONFIGURATION_FILE);
		} catch (ProxoolException e) {
			LOGGER.fatal("Could not configure proxool db connection", e);
		}
	}
	
	public void setConf(Configuration conf) {
		this.conf = conf;
	}
	
	public void run() throws ComponentException {
		ExternalComponentManager manager = new ExternalComponentManager(
		        this.hostname, this.socket);
		manager.setSecretKey(this.domainName, this.password);
		manager.addComponent(this.domainName, new ChannelsEngine(conf));
	}
}