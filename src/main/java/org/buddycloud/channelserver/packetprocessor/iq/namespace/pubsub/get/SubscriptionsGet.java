package org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.get;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.buddycloud.channelserver.channel.OperationsFactory;
import org.buddycloud.channelserver.federation.AsyncCall.ResultHandler;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.PubSubElementProcessor;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.PubSubGet;
import org.buddycloud.channelserver.pubsub.model.NodeSubscription;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;

public class SubscriptionsGet implements PubSubElementProcessor {

	private final BlockingQueue<Packet> outQueue;
	private final OperationsFactory operations;

	private static final Logger LOGGER = Logger
			.getLogger(SubscriptionsGet.class);

	public SubscriptionsGet(BlockingQueue<Packet> outQueue,
			OperationsFactory operations) {
		this.outQueue = outQueue;
		this.operations = operations;
	}

	@Override
	public void process(final Element elm, JID actorJID, final IQ reqIQ, final Element rsm)
			throws Exception {
		final String node = elm.attributeValue("node");

		if (actorJID == null) {
			actorJID = reqIQ.getFrom();
		}
		
		ResultHandler<Collection<NodeSubscription>> handler = new ResultHandler<Collection<NodeSubscription>>() {
			
			@Override
			public void onSuccess(Collection<NodeSubscription> result) {
				try {
					returnSubscriptions(reqIQ, elm, result, node);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				
			}
		};

		if (node == null) {
			operations.getUserSubscriptions(actorJID).call(handler);
		} else {
			operations.getNodeSubscriptions(node).call(handler);
		}
	}
	
	private void returnSubscriptions(final IQ requestIQ, final Element requestEl, final Collection<NodeSubscription> subscriptions, final String node) throws InterruptedException {
		IQ result = IQ.createResultIQ(requestIQ);
		Element pubsub = result.setChildElement(PubSubGet.ELEMENT_NAME,
				requestEl.getNamespaceURI());
		Element subscriptionsEl = pubsub.addElement("subscriptions");

		if(node != null) {
			subscriptionsEl.addAttribute("node", node);
		}

		for (NodeSubscription ns : subscriptions) {

			subscriptionsEl
					.addElement("subscription")
					.addAttribute("node", ns.getNodeId())
					.addAttribute("subscription",
							ns.getSubscription().toString())
					.addAttribute("jid", ns.getUser().toBareJID());
		}
		
		outQueue.put(result);
	}

	@Override
	public boolean accept(Element elm) {
		return elm.getName().equals("subscriptions");
	}
}
