package org.buddycloud.channelserver.federation.requests.pubsub;

import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.federation.ServiceDiscoveryRegistry;
import org.xmpp.packet.JID;

public class GetUserSubscriptions extends GetSubscriptions {

	public GetUserSubscriptions(ServiceDiscoveryRegistry discovery,
			XMPPConnection connection, ChannelManager channelManager, final JID user) {
		super(connection, discovery, channelManager, user);
	}

}
