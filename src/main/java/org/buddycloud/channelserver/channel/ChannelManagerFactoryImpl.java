package org.buddycloud.channelserver.channel;

import org.buddycloud.channelserver.Configuration;
import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.db.NodeStoreFactory;
import org.buddycloud.channelserver.federation.ServiceDiscoveryRegistry;

public class ChannelManagerFactoryImpl implements ChannelManagerFactory {

	private final Configuration configuration;
	private final NodeStoreFactory nodeStoreFactory;
	private final XMPPConnection connection;
	private final ServiceDiscoveryRegistry registry;

	public ChannelManagerFactoryImpl(final Configuration configuration,
			final NodeStoreFactory nodeStoreFactory,
			final XMPPConnection connection,
			final ServiceDiscoveryRegistry registry) {
		this.configuration = configuration;
		this.nodeStoreFactory = nodeStoreFactory;
		this.connection = connection;
		this.registry = registry;
	}

	@Override
	public ChannelManager create() {
		ChannelManager channelManager = new ChannelManagerImpl(
				nodeStoreFactory.create(), configuration);

		OperationsFactory operations = new OperationsFactory(registry, connection, channelManager);
		
		return channelManager;
	}
}