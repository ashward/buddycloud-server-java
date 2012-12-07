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

package org.buddycloud.channelserver.packetprocessor.iq.namespace.discoinfo;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.packetprocessor.PacketProcessor;
import org.dom4j.Element;
import org.xmpp.forms.DataForm;
import org.xmpp.forms.FormField;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;

public class DiscoInfoGet implements PacketProcessor<IQ> {

	public static final String ELEMENT_NAME = "query";
	private static final Logger LOGGER = Logger.getLogger(DiscoInfoGet.class);
	private final BlockingQueue<Packet> outQueue;
	private final ChannelManager channelManager;
	private String node;

	private IQ requestIq;
	
	public DiscoInfoGet(BlockingQueue<Packet> outQueue, ChannelManager channelManager) {
		this.outQueue = outQueue;
		this.channelManager = channelManager;
	}

	@Override
	public void process(IQ reqIQ) throws Exception {

		requestIq = reqIQ;
		IQ result   = IQ.createResultIQ(reqIQ);
		Element elm = reqIQ.getChildElement();
		node = elm.attributeValue("node");
		
		if ((node == null) || (true == node.equals(""))) {
			Element query = result.setChildElement(ELEMENT_NAME,
					JabberDiscoInfo.NAMESPACE_URI);
			query.addElement("identity").addAttribute("category", "pubsub")
					.addAttribute("type", "channels");

			query.addElement("identity").addAttribute("category", "pubsub")
					.addAttribute("type", "inbox");

			query.addElement("feature").addAttribute("var",
					"jabber:iq:register");

			query.addElement("feature").addAttribute("var",
					"http://jabber.org/protocol/disco#info");

			outQueue.put(result);
			return;
		}

		if (false == channelManager.isLocalNode(node)) {
			makeRemoteRequest();
		    return;
		}
		
		Map<String, String> conf = channelManager.getNodeConf(node);
		if (conf.isEmpty()) {
/*
			// Add the possibility to do disco info on foreign node.
			// Only available for local users.
			if (channelManager.isLocalUser(reqIQ.getFrom().toBareJID())) {

				// If we are here, it means we have a node that was not on this
				// channel server
				// but the user was local. Let's try to find it from a foreign
				// node.

				DiscoInfo di = DiscoInfo.buildDiscoInfoStatemachine(node,
						reqIQ, channelManager);
				outQueue.put(di.nextStep());
				return;

			}
*/
			/*
			 * Not found. Let's return something like this: <iq type='error'
			 * from='plays.shakespeare.lit' to='romeo@montague.net/orchard'
			 * id='info1'> <query xmlns='http://jabber.org/protocol/disco#info'
			 * node='/user/pretty@lady.com/posts'/> <error type='cancel'>
			 * <item-not-found xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>
			 * </error> </iq>
			 */

			IQ reply = IQ.createResultIQ(reqIQ);
			reply.setChildElement(reqIQ.getChildElement().createCopy());
			reply.setType(Type.error);
			PacketError pe = new PacketError(
					org.xmpp.packet.PacketError.Condition.item_not_found,
					org.xmpp.packet.PacketError.Type.cancel);
			reply.setError(pe);
			outQueue.put(reply);
			return;
		}

		TreeMap<String, String> sorted_conf = new TreeMap<String, String>();

		DataForm x = new DataForm(DataForm.Type.result);
		FormField formType = x.addField();
		formType.setType(FormField.Type.hidden);
		formType.setVariable("FORM_TYPE");
		formType.addValue("http://jabber.org/protocol/pubsub#meta-data");

		sorted_conf.putAll(conf);
		for (String key : sorted_conf.keySet()) {
			x.addField(key, null, null).addValue(sorted_conf.get(key));
		}

		Element query = result.setChildElement(ELEMENT_NAME,
				JabberDiscoInfo.NAMESPACE_URI);
		query.addAttribute("node", node);
		query.addElement("identity").addAttribute("category", "pubsub")
				.addAttribute("type", "leaf");
		query.addElement("feature").addAttribute("var",
				"http://jabber.org/protocol/pubsub");

		query.add(x.getElement());
        LOGGER.trace("Returning DISCO info for node: " + node);
		outQueue.put(result);
	}

	private void makeRemoteRequest() throws InterruptedException {
		requestIq.setTo(new JID(node.split("/")[2]).getDomain());
	    outQueue.put(requestIq);
	}
}