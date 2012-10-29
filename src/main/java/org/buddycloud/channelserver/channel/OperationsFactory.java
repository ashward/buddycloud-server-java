package org.buddycloud.channelserver.channel;

import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.federation.ServiceDiscoveryRegistry;
import org.buddycloud.channelserver.federation.requests.pubsub.GetNodeItems;
import org.buddycloud.channelserver.federation.requests.pubsub.GetNodeSubscriptions;
import org.buddycloud.channelserver.federation.requests.pubsub.GetUserAffiliations;
import org.buddycloud.channelserver.federation.requests.pubsub.GetUserSubscriptions;
import org.buddycloud.channelserver.federation.requests.pubsub.NodeExists;
import org.buddycloud.channelserver.utils.request.Parameters;
import org.xmpp.packet.JID;

public class OperationsFactory {
	private final ServiceDiscoveryRegistry discovery;
	private final XMPPConnection connection;
	private final ChannelManager channelManager;
	
	public OperationsFactory(ServiceDiscoveryRegistry discovery,
			XMPPConnection connection, ChannelManager channelManager) {
		this.discovery = discovery;
		this.connection = connection;
		this.channelManager = channelManager;
	}

	public GetNodeItems getNodeItems(final String nodeId) {
		return new GetNodeItems(discovery, connection, channelManager, nodeId);
	}
	
	public NodeExists nodeExists(final String nodeId) {
		return new NodeExists(discovery, connection, channelManager, nodeId);
	}
	
	public GetUserAffiliations getUserAffiliations(final JID user) {
		return new GetUserAffiliations(connection, discovery, channelManager, user);
	}
	
	public GetNodeSubscriptions getNodeSubscriptions(final String nodeId) {
		return new GetNodeSubscriptions(discovery, connection, channelManager, nodeId);
	}
	
	public GetUserSubscriptions getUserSubscriptions(final JID user) {
		return new GetUserSubscriptions(discovery, connection, channelManager, user);
	}
}
