package org.buddycloud.channelserver.channel;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.db.CloseableIterator;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.federation.AsyncCall;
import org.buddycloud.channelserver.federation.AsyncCall.ResultHandler;
import org.buddycloud.channelserver.federation.ServiceDiscoveryRegistry;
import org.buddycloud.channelserver.pubsub.affiliation.Affiliations;
import org.buddycloud.channelserver.pubsub.model.NodeAffiliation;
import org.buddycloud.channelserver.pubsub.model.NodeItem;
import org.buddycloud.channelserver.pubsub.model.NodeSubscription;
import org.buddycloud.channelserver.utils.request.Parameters;
import org.xmpp.packet.JID;

/**
 * TODO: Get rid of this class ASAP!
 */
public class FederatedChannelManager implements ChannelManager {

	private final ChannelManager delegate;
	private final OperationsFactory operations;
	private Parameters requestParameters;

	private int timeoutMillis = 60000;

	private static final Logger LOGGER = Logger
			.getLogger(FederatedChannelManager.class);

	public FederatedChannelManager(final ChannelManager delgate,
			final XMPPConnection xmppConnection,
			final ServiceDiscoveryRegistry discoveryRegistry,
			final OperationsFactory operationsFactory) {
		this.delegate = delgate;
		this.operations = operationsFactory;
	}

	/**
	 * Sets the number of milliseconds to wait before timing out an asyncronous
	 * call.
	 * 
	 * @param millis
	 *            the number of milliseconds.
	 */
	public void setTimeoutMillis(final int millis) {
		this.timeoutMillis = millis;
	}

	@Override
	public void createNode(JID owner, String nodeId,
			Map<String, String> nodeConf) throws NodeStoreException {
		delegate.createNode(owner, nodeId, nodeConf);
	}

	@Override
	public void setNodeConfValue(String nodeId, String key, String value)
			throws NodeStoreException {
		delegate.setNodeConfValue(nodeId, key, value);
	}

	@Override
	public void setNodeConf(String nodeId, Map<String, String> conf)
			throws NodeStoreException {
		delegate.setNodeConf(nodeId, conf);
	}

	@Override
	public String getNodeConfValue(String nodeId, String key)
			throws NodeStoreException {
		// TODO Auto-generated method stub
		return delegate.getNodeConfValue(nodeId, key);
	}

	@Override
	public Map<String, String> getNodeConf(String nodeId)
			throws NodeStoreException {
		// TODO Auto-generated method stub
		return delegate.getNodeConf(nodeId);
	}

	@Override
	public boolean nodeExists(String nodeId) throws NodeStoreException {
		return runSynchronously(operations.nodeExists(nodeId));
	}

	@Override
	public void setUserAffiliation(String nodeId, JID user,
			Affiliations affiliation) throws NodeStoreException {
		delegate.setUserAffiliation(nodeId, user, affiliation);
	}

	@Override
	public void addUserSubscription(NodeSubscription subscription)
			throws NodeStoreException {
		delegate.addUserSubscription(subscription);

	}

	@Override
	public NodeAffiliation getUserAffiliation(String nodeId, JID user)
			throws NodeStoreException {
		// TODO Auto-generated method stub
		return delegate.getUserAffiliation(nodeId, user);
	}

	@Override
	public Collection<NodeAffiliation> getUserAffiliations(JID user)
			throws NodeStoreException {
		// TODO Auto-generated method stub
		return delegate.getUserAffiliations(user);
	}

	@Override
	public Collection<NodeAffiliation> getNodeAffiliations(String nodeId)
			throws NodeStoreException {
		// TODO Auto-generated method stub
		return delegate.getNodeAffiliations(nodeId);
	}

	@Override
	public Collection<NodeSubscription> getUserSubscriptions(JID user)
			throws NodeStoreException {
		// TODO Auto-generated method stub
		return delegate.getUserSubscriptions(user);
	}

	@Override
	public Collection<NodeSubscription> getNodeSubscriptions(String nodeId)
			throws NodeStoreException {
		// TODO Auto-generated method stub
		return delegate.getNodeSubscriptions(nodeId);
	}

	@Override
	public NodeSubscription getUserSubscription(String nodeId, JID user)
			throws NodeStoreException {
		// TODO Auto-generated method stub
		return delegate.getUserSubscription(nodeId, user);
	}

	@Override
	public CloseableIterator<NodeItem> getNodeItems(String nodeId,
			String afterItemId, int count) throws NodeStoreException {
		return runSynchronously(operations.getNodeItems(nodeId));
	}

	@Override
	public CloseableIterator<NodeItem> getNodeItems(String nodeId)
			throws NodeStoreException {
		return getNodeItems(nodeId, null, 30);
	}

	@Override
	public int countNodeItems(String nodeId) throws NodeStoreException {
		// TODO Auto-generated method stub
		return delegate.countNodeItems(nodeId);
	}

	@Override
	public NodeItem getNodeItem(String nodeId, String nodeItemId)
			throws NodeStoreException {
		// TODO Auto-generated method stub
		return delegate.getNodeItem(nodeId, nodeItemId);
	}

	@Override
	public void addNodeItem(NodeItem item) throws NodeStoreException {
		delegate.addNodeItem(item);
	}

	@Override
	public void updateNodeItem(NodeItem item) throws NodeStoreException {
		delegate.updateNodeItem(item);
	}

	@Override
	public void deleteNodeItemById(String nodeId, String nodeItemId)
			throws NodeStoreException {
		delegate.deleteNodeItemById(nodeId, nodeItemId);
	}

	@Override
	public void close() throws NodeStoreException {
		delegate.close();
	}

	@Override
	public Transaction beginTransaction() throws NodeStoreException {
		return delegate.beginTransaction();
	}

	@Override
	public void createPersonalChannel(JID ownerJID) throws NodeStoreException {
		delegate.createPersonalChannel(ownerJID);
	}

	@Override
	public boolean isLocalNode(String nodeId) throws NodeStoreException {
		return delegate.isLocalNode(nodeId);
	}

	@Override
	public boolean isLocalJID(JID jid) throws NodeStoreException {
		return delegate.isLocalJID(jid);
	}

	@Override
	public void setRequestParameters(Parameters requestParameters) {
		this.requestParameters = requestParameters;
	}

	public Parameters getRequestParameters() {
		return requestParameters;
	}

	/**
	 * Takes an {@link AsyncCall} by calling
	 * {@link AsyncCall#call(ResultHandler)} and runs it synchronously -
	 * blocking until it has completed.
	 * 
	 * @param operation
	 *            the {@link AsyncCall} to run.
	 * @return the result of the call.
	 * @throws NodeStoreException
	 *             if something went wrong.
	 */
	private <T extends AsyncCall<K>, K> K runSynchronously(final T operation)
			throws NodeStoreException {
		final ObjectHolder<K> result = new ObjectHolder<K>();
		final ObjectHolder<Throwable> error = new ObjectHolder<Throwable>();
		
		// This is a semaphore to tell the inner handler that the outer has timed out.
		final ObjectHolder<Boolean> timedOut = new ObjectHolder<Boolean>();
		timedOut.set(false);

		final Thread thread = Thread.currentThread();

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Calling operation of type " + operation.getClass() + ": [" + operation.toString() + "]");
		}
		
		try {
			operation.call(new ResultHandler<K>() {
	
				@Override
				public void onSuccess(final K data) {
					if(LOGGER.isDebugEnabled()) {
						LOGGER.debug("Successfully received data from the AsyncCall: " + data);
					}
					result.set(data);

					// If Thread.sleep has timed out then we don't want to interrupt the thread
					if(!timedOut.get()) {
						thread.interrupt();
					}
				}
	
				@Override
				public void onError(final Throwable t) {
					if(LOGGER.isDebugEnabled()) {
						LOGGER.debug("Got an error from the AsyncCall", t);
					}
					error.set(t);

					// If Thread.sleep has timed out then we don't want to interrupt the thread
					if(!timedOut.get()) {
						thread.interrupt();
					}
				}
			});
	
			Thread.sleep(timeoutMillis);
		} catch (InterruptedException e) {
			if (result.get() != null) {
				return result.get();
			}

			if (error.get() instanceof NodeStoreException) {
				throw (NodeStoreException) error.get();
			} else {
				throw new NodeStoreException("Unexpected error caught",
						error.get());
			}
		} finally {
			timedOut.set(true);
		}
		throw new NodeStoreException("Timed out");

	}

	/**
	 * Holds a reference to an object. This is used to pass objects from an
	 * inner class method to the outer class.
	 * 
	 * @param <T>
	 *            the type of the object to hold.
	 */
	private class ObjectHolder<T> {
		private T obj;

		public T get() {
			return obj;
		}

		public synchronized void set(final T obj) {
			this.obj = obj;
		}
	}
}