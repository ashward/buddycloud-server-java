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
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.JabberPubsub;
import org.dom4j.Element;
import org.xmpp.component.Component;
import org.xmpp.component.ComponentException;
import org.xmpp.component.ComponentManager;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;

public class TopicsEngine implements Component {

	private static final Logger logger = Logger.getLogger(TopicsEngine.class);

	private JID jid = null;
	private ComponentManager manager = null;

	private Properties configuration;

	public TopicsEngine(Properties conf) {
		this.configuration = conf;
	}

	@Override
	public String getDescription() {
		return "buddycloud channel server (Java implementation) - topics component";
	}

	@Override
	public String getName() {
		return "buddycloud-topics-component";
	}

	@Override
	public void initialize(JID jid, ComponentManager manager)
			throws ComponentException {

		this.jid = jid;
		this.manager = manager;

		logger.info("XMPP Component started. We are '" + jid.toString()
				+ "' and ready to accept packages.");
	}

	@Override
	public void processPacket(Packet p) {
		try {
			if (p instanceof IQ) {
				if (p.getElement().element("query").getNamespace().getStringValue().equals(JabberPubsub.NS_DISCO_INFO)) {
					// TODO support info requests
				} else if (p.getElement().element("query").getNamespace().getStringValue().equals(JabberPubsub.NS_DISCO_ITEMS)) {
					IQ response = IQ.createResultIQ((IQ) p);
					Element query = response.getElement().addElement("query");
			        query.addNamespace("",  JabberPubsub.NS_DISCO_ITEMS);
			        Element item = query.addElement("item");
			        item.addAttribute("name",  "buddycloud-server");
			        item.addAttribute("jid", configuration.getProperty("server.domain.channels"));
			        this.sendPacket(response);
			        return;
				}
			}

		} catch (NullPointerException e) {
			// Catch and ignore
			e.printStackTrace();
		} catch (ComponentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Topic component does not handle these packets: '" + p.toXML()
				+ "'.");
	}

	public void sendPacket(Packet p) throws ComponentException {
		manager.sendPacket(this, p);
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
	}

	@Override
	public void start() {
		/**
		 * Notification message indicating that the component will start
		 * receiving incoming packets. At this time the component may finish
		 * pending initialization issues that require information obtained from
		 * the server.
		 * 
		 * It is likely that most of the component will leave this method empty.
		 */
	}

	public JID getJID() {
		return this.jid;
	}
}