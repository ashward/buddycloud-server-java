package org.buddycloud.channelserver.channel;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.db.CloseableIterator;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.federation.ServiceDiscoveryRegistry;
import org.buddycloud.channelserver.federation.AsyncCall.ResultHandler;
import org.buddycloud.channelserver.federation.requests.pubsub.GetNodeItems;
import org.buddycloud.channelserver.federation.requests.pubsub.NodeExists;
import org.buddycloud.channelserver.pubsub.model.NodeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.omg.CORBA.portable.ResponseHandler;
import org.xmpp.packet.IQ;

public class FederatedChannelManagerTest {

	private static final String TEST_LOCAL_NODE_ID = "users/user@server.com/posts";
	private static final String TEST_REMOTE_NODE_ID = "users/user@otherserver.com/posts";

	@Mock
	ChannelManager channelManager;

	@Mock
	XMPPConnection xmppConnection;

	@Mock
	ServiceDiscoveryRegistry discoveryRegistry;

	@Mock
	OperationsFactory operations;

	/**
	 * Class under test
	 */
	FederatedChannelManager federatedChannelManager;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		federatedChannelManager = new FederatedChannelManager(channelManager,
				xmppConnection, discoveryRegistry, operations);
		federatedChannelManager.setTimeoutMillis(60000);
	}
	
	@After
	public void tearDown() throws Exception {
		federatedChannelManager = null;
		channelManager = null;
		xmppConnection = null;
		discoveryRegistry = null;
		operations = null;
	}

	@Test
	public void testGetNodeItemsString() throws Exception {
		@SuppressWarnings("unchecked")
		final CloseableIterator<NodeItem> expected = mock(CloseableIterator.class);
		
		when(operations.getNodeItems(TEST_REMOTE_NODE_ID)).thenReturn(new GetNodeItems(discoveryRegistry, xmppConnection, TEST_REMOTE_NODE_ID, channelManager) {

			@Override
			public void call(final ResultHandler<CloseableIterator<NodeItem>> handler) {
				scheduleSuccess(handler, expected);
			}
			
		});
		
		CloseableIterator<NodeItem> result = federatedChannelManager.getNodeItems(TEST_REMOTE_NODE_ID);
		
		assertEquals(expected, result);
	}

	@Test(expected=NodeStoreException.class)
	public void testGetNodeItemsStringFailure() throws Exception {
		final Throwable expected = mock(Throwable.class);
		
		when(operations.getNodeItems(TEST_REMOTE_NODE_ID)).thenReturn(new GetNodeItems(discoveryRegistry, xmppConnection, TEST_REMOTE_NODE_ID, channelManager) {

			@Override
			public void call(final ResultHandler<CloseableIterator<NodeItem>> handler) {
				scheduleFailure(handler, expected);
			}
			
		});
		
		federatedChannelManager.getNodeItems(TEST_REMOTE_NODE_ID);
	}

	@Test(expected=NodeStoreException.class)
	public void testGetNodeItemsStringTimeout() throws Exception {
		@SuppressWarnings("unchecked")
		final CloseableIterator<NodeItem> expected = mock(CloseableIterator.class);
		
		federatedChannelManager.setTimeoutMillis(5);
		
		when(operations.getNodeItems(TEST_REMOTE_NODE_ID)).thenReturn(new GetNodeItems(discoveryRegistry, xmppConnection, TEST_REMOTE_NODE_ID, channelManager) {

			@Override
			public void call(final ResultHandler<CloseableIterator<NodeItem>> handler) {
				scheduleSuccess(handler, expected);
			}
			
		});
		
		federatedChannelManager.getNodeItems(TEST_REMOTE_NODE_ID);
	}
	
	@Test
	public void testNodeExists() throws Exception {
		final Boolean expected = new Boolean(true);
		
		when(operations.nodeExists(TEST_REMOTE_NODE_ID)).thenReturn(new NodeExists(discoveryRegistry, xmppConnection, TEST_REMOTE_NODE_ID, channelManager) {

			@Override
			public void call(final ResultHandler<Boolean> handler) {
				scheduleSuccess(handler, expected);
			}
			
		});
		
		Boolean result = federatedChannelManager.nodeExists(TEST_REMOTE_NODE_ID);
		
		assertEquals(expected, result);
	}

	@Test(expected=NodeStoreException.class)
	public void testNodeExistsFailure() throws Exception {
		final Throwable expected = mock(Throwable.class);
		
		when(operations.nodeExists(TEST_REMOTE_NODE_ID)).thenReturn(new NodeExists(discoveryRegistry, xmppConnection, TEST_REMOTE_NODE_ID, channelManager) {

			@Override
			public void call(final ResultHandler<Boolean> handler) {
				scheduleFailure(handler, expected);
			}
			
		});
		
		federatedChannelManager.nodeExists(TEST_REMOTE_NODE_ID);
	}
	
	private <T> void scheduleSuccess(final ResultHandler<T> handler, final T toReturn) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// Ignore
				}
				handler.onSuccess(toReturn);
			}
			
		}).start();
	}
	
	private void scheduleFailure(final ResultHandler<?> handler, final Throwable toReturn) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// Ignore
				}
				handler.onError(toReturn);
			}
			
		}).start();
	}
}
