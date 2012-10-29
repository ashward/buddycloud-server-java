package org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.get;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.channel.OperationsFactory;
import org.buddycloud.channelserver.federation.AsyncCall.ResultHandler;
import org.buddycloud.channelserver.federation.requests.pubsub.GetUserAffiliations;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.JabberPubsub;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.PubSubElementProcessor;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.PubSubGet;
import org.buddycloud.channelserver.pubsub.model.NodeAffiliation;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.packet.IQ.Type;

public class AffiliationsGet implements PubSubElementProcessor {

	private final BlockingQueue<Packet> outQueue;
	private final OperationsFactory operations;

	private static final Logger LOGGER = Logger
			.getLogger(AffiliationsGet.class);

	public AffiliationsGet(BlockingQueue<Packet> outQueue,
			OperationsFactory operations) {
		this.outQueue = outQueue;
		this.operations = operations;
	}

	@Override
	public void process(final Element elm, JID actorJID, final IQ reqIQ, final Element rsm) {
		if (actorJID == null) {
			actorJID = reqIQ.getFrom();
		}
		
		String node = elm.attributeValue("node");

		if (node == null) {
			LOGGER.debug("Creating GetUserAffilitions");
			GetUserAffiliations getUserAffiliations = operations.getUserAffiliations(actorJID);
		
			getUserAffiliations.call(new ResultHandler<Collection<NodeAffiliation>>() {
				
				@Override
				public void onSuccess(Collection<NodeAffiliation> result) {
					LOGGER.debug("GetUserAffiliations returned success");
					try {
						returnAffiliations(reqIQ, result);
					} catch (InterruptedException e) {
						LOGGER.error("Unexpected InterruptedException caught", e);
					}
				}
				
				@Override
				public void onError(Throwable t) {
					LOGGER.debug("GetUserAffiliations returned error");
					// TODO Different types of error
					IQ reply = IQ.createResultIQ(reqIQ);
					reply.setType(Type.error);

					Element badRequest = new DOMElement("bad-request",
							new org.dom4j.Namespace("", JabberPubsub.NS_XMPP_STANZAS));

					Element nodeIdRequired = new DOMElement("nodeid-required",
							new org.dom4j.Namespace("", JabberPubsub.NS_PUBSUB_ERROR));

					Element error = new DOMElement("error");
					error.addAttribute("type", "modify");
					error.add(badRequest);
					error.add(nodeIdRequired);

					reply.setChildElement(error);

					try {
						outQueue.put(reply);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		} else {
			// TODO Different types of error
			IQ reply = IQ.createResultIQ(reqIQ);
			reply.setType(Type.error);

			Element badRequest = new DOMElement("bad-request",
					new org.dom4j.Namespace("", JabberPubsub.NS_XMPP_STANZAS));

			Element nodeIdRequired = new DOMElement("nodeid-required",
					new org.dom4j.Namespace("", JabberPubsub.NS_PUBSUB_ERROR));

			Element error = new DOMElement("error");
			error.addAttribute("type", "modify");
			error.add(badRequest);
			error.add(nodeIdRequired);

			reply.setChildElement(error);

			try {
				outQueue.put(reply);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO s/User/Node/
			/*
			GetUserAffiliations getUserAffiliations = operations.getUserAffiliations(actorJID);
			
			getUserAffiliations.call(new ResultHandler<Collection<NodeAffiliation>>() {
				
				@Override
				public void onSuccess(Collection<NodeAffiliation> result) {
					try {
						returnAffiliations(reqIQ, result);
					} catch (InterruptedException e) {
						LOGGER.error("Unexpected InterruptedException caught", e);
					}
				}
				
				@Override
				public void onError(Throwable t) {
					// TODO Auto-generated method stub
					
				}
			});
			*/
		}
	}
	
	private void returnAffiliations(final IQ requestIQ, final Collection<NodeAffiliation> affiliations) throws InterruptedException {
		IQ result = IQ.createResultIQ(requestIQ);
		Element pubsub = result.setChildElement(PubSubGet.ELEMENT_NAME,
				JabberPubsub.NS_PUBSUB_OWNER);
		Element affiliationsEl = pubsub.addElement("affiliations");

		for (NodeAffiliation aff : affiliations) {
			LOGGER.trace("Adding affiliation for "
					+ aff.getUser() + " affiliation "
					+ aff.getAffiliation() + " (no node provided)");
			affiliationsEl
					.addElement("affiliation")
					.addAttribute("node", aff.getNodeId())
					.addAttribute("affiliation",
							aff.getAffiliation().toString())
					.addAttribute("jid", aff.getUser().toString());
		}
		outQueue.put(result);
	}

	@Override
	public boolean accept(Element elm) {
		return elm.getName().equals("affiliations");
	}
}
