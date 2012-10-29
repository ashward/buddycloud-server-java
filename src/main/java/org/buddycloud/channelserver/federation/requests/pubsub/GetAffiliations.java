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
import org.buddycloud.channelserver.pubsub.affiliation.Affiliations;
import org.buddycloud.channelserver.pubsub.model.NodeAffiliation;
import org.buddycloud.channelserver.pubsub.model.impl.NodeAffiliationImpl;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;

public class GetAffiliations extends
		ChannelServerRequestAbstract<Collection<NodeAffiliation>> {

	private final String nodeId;
	private final JID user;
	private final ChannelNodeRef nodeRef;

	public GetAffiliations(final XMPPConnection connection,
			final ServiceDiscoveryRegistry discovery,
			ChannelManager channelManager, final String nodeId) {
		super(discovery, connection, channelManager);
		this.nodeId = nodeId;
		this.nodeRef = ChannelNodeRef.fromNodeId(nodeId);
		this.user = null;
	}

	public GetAffiliations(final XMPPConnection connection,
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
		} else {
			return user;
		}
	}

	@Override
	protected void sendRequest(final JID remoteServer,
			final ResultHandler<Collection<NodeAffiliation>> handler) {
		IQ iq = new IQ(IQ.Type.get);

		iq.setTo(remoteServer);

		Element pubsub = iq.setChildElement("pubsub",
				JabberPubsub.NAMESPACE_URI);

		Element affiliations = pubsub.addElement("affiliations");
		affiliations.addAttribute("node", nodeId);
		Element actor = pubsub.addElement("actor");
		actor.addAttribute("jid", channelManager.getRequestParameters().getRequester().toBareJID());
		actor.addNamespace("", Buddycloud.NAMESPACE);

		sendIq(iq, handler);
	}

	@Override
	protected Collection<NodeAffiliation> fromIq(IQ iq) {
		ArrayList<NodeAffiliation> result = new ArrayList<NodeAffiliation>();
		NodeAffiliation nodeAffiliation;
		String nodeId = iq.getElement().element("affiliations")
				.attributeValue("node");
		JID jid;
		try {
			@SuppressWarnings("unchecked")
			List<Element> affiliationsList = iq.getElement().elements(
					"affiliation");
			for (Element affiliation : affiliationsList) {
				jid = new JID(affiliation.attributeValue("jid"));
				nodeAffiliation = new NodeAffiliationImpl(nodeId, jid,
						Affiliations.valueOf(affiliation
								.attributeValue("affiliation")));
				result.add(nodeAffiliation);
			}
		} catch (NullPointerException e) {
			// TODO NPE shouldn't mean no items!
			// Means no items
		}
		return result;
	}
}