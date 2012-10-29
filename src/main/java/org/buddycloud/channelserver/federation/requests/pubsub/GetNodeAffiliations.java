package org.buddycloud.channelserver.federation.requests.pubsub;

import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.federation.ServiceDiscoveryRegistry;

public class GetNodeAffiliations extends
		GetAffiliations {
	public GetNodeAffiliations(final XMPPConnection connection,
			final ServiceDiscoveryRegistry discovery,
			ChannelManager channelManager, final String nodeId) {
		super(connection, discovery, channelManager, nodeId);
	}
}