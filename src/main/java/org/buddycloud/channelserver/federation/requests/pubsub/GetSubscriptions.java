package org.buddycloud.channelserver.federation.requests.pubsub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.channel.ChannelNodeRef;
import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.federation.ChannelServerRequestAbstract;
import org.buddycloud.channelserver.federation.ServiceDiscoveryRegistry;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.Buddycloud;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.JabberPubsub;
import org.buddycloud.channelserver.pubsub.model.NodeSubscription;
import org.buddycloud.channelserver.pubsub.model.impl.NodeSubscriptionImpl;
import org.buddycloud.channelserver.pubsub.subscription.Subscriptions;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;

class GetSubscriptions extends
		ChannelServerRequestAbstract<Collection<NodeSubscription>> {

	private final String nodeId;
	private final JID user;
	private final ChannelNodeRef nodeRef;

	public GetSubscriptions(final XMPPConnection connection,
			final ServiceDiscoveryRegistry discovery,
			ChannelManager channelManager, final String nodeId) {
		super(discovery, connection, channelManager);
		this.nodeId = nodeId;
		this.nodeRef = ChannelNodeRef.fromNodeId(nodeId);
		this.user = null;
	}
	
	public GetSubscriptions(final XMPPConnection connection,
			final ServiceDiscoveryRegistry discovery,
			ChannelManager channelManager, final JID user) {
		super(discovery, connection, channelManager);
		this.nodeId = null;
		this.nodeRef = null;
		this.user = user;
	}

	@Override
	protected JID getToJid() {
		if(nodeRef != null) {
			return nodeRef.getJID();
		} else if(user != null) {
			return user;
		} else {
			return super.getToJid();
		}
	}

	@Override
	protected void sendRequest(final JID remoteServer,
			final ResultHandler<Collection<NodeSubscription>> handler) {
		IQ iq = new IQ(IQ.Type.get);

		iq.setTo(remoteServer);

		Element pubsub = iq.setChildElement("pubsub",
				JabberPubsub.NAMESPACE_URI);

		Element affiliations = pubsub.addElement("subscriptions");
		affiliations.addAttribute("node", nodeId);
		Element actor = pubsub.addElement("actor");
		actor.addAttribute("jid", channelManager.getRequestParameters().getRequester().toBareJID());
		actor.addNamespace("", Buddycloud.NAMESPACE);

		sendIq(iq, handler);
	}

	@Override
	protected Collection<NodeSubscription> fromIq(IQ iq) {
		ArrayList<NodeSubscription> result = new ArrayList<NodeSubscription>();
		NodeSubscription nodeAffiliation;
		String nodeId = iq.getElement().element("affiliations")
				.attributeValue("node");
		JID jid;
		try {
			@SuppressWarnings("unchecked")
			List<Element> affiliationsList = iq.getElement().elements(
					"affiliation");
			for (Element affiliation : affiliationsList) {
				jid = new JID(affiliation.attributeValue("jid"));
				nodeAffiliation = new NodeSubscriptionImpl(nodeId, jid,
						Subscriptions.valueOf(affiliation
								.attributeValue("subscription")));
				result.add(nodeAffiliation);
			}
		} catch (NullPointerException e) {
			// Means no items
		}
		return result;
	}
}