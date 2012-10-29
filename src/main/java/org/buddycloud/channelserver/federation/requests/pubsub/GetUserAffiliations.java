package org.buddycloud.channelserver.federation.requests.pubsub;

import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.federation.ServiceDiscoveryRegistry;
import org.xmpp.packet.JID;

public class GetUserAffiliations extends GetAffiliations {

	public GetUserAffiliations(final XMPPConnection connection,
			final ServiceDiscoveryRegistry discovery,
			ChannelManager channelManager, final JID user) {
		super(connection, discovery, channelManager, user);
	}
}