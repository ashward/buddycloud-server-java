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

import java.util.Properties;

import org.apache.log4j.Logger;
import org.jivesoftware.whack.ExternalComponentManager;
import org.xmpp.component.ComponentException;

public class TopicsComponent {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(TopicsComponent.class);
	private String hostname;
	private int socket;
	
	private String domain;
	private String password;
	private TopicsEngine topicsEngine;
	
	public TopicsComponent(Properties configuration, String domain) {
		if (null == domain) {
			return;
		}
		hostname = configuration.getProperty("xmpp.host");
		socket = Integer.valueOf(configuration.getProperty("xmpp.port"));
		this.domain = domain;
		password = configuration.getProperty("xmpp.secretkey");
		topicsEngine = new TopicsEngine(configuration);

	}
	
	public void run() throws ComponentException {
		ExternalComponentManager manager = new ExternalComponentManager(
		        hostname, socket);
		manager.setDefaultSecretKey(password);
		manager.addComponent(domain, topicsEngine);
	}
}