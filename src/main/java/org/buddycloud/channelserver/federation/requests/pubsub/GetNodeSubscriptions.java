package org.buddycloud.channelserver.federation.requests.pubsub;

import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.federation.ServiceDiscoveryRegistry;

public class GetNodeSubscriptions extends GetSubscriptions {

	public GetNodeSubscriptions(final ServiceDiscoveryRegistry discovery,
			final XMPPConnection connection, final ChannelManager channelManager, final String nodeId) {
		super(connection, discovery, channelManager, nodeId);
	}

}
