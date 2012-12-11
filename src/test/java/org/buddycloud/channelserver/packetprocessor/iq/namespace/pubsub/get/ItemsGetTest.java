/*
 * Buddycloud Channel Server
 * http://buddycloud.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.get;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.db.mock.Mock;
import org.buddycloud.channelserver.packetHandler.iq.IQTestHandler;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.JabberPubsub;
import org.buddycloud.channelserver.pubsub.accessmodel.AccessModels;
import org.buddycloud.channelserver.pubsub.affiliation.Affiliations;
import org.buddycloud.channelserver.pubsub.model.NodeItem;
import org.buddycloud.channelserver.pubsub.model.NodeSubscription;
import org.buddycloud.channelserver.pubsub.model.impl.NodeAffiliationImpl;
import org.buddycloud.channelserver.pubsub.model.impl.NodeSubscriptionImpl;
import org.buddycloud.channelserver.pubsub.subscription.Subscriptions;
import org.buddycloud.channelserver.utils.node.NodeAclRefuseReason;
import org.buddycloud.channelserver.utils.node.NodeViewAcl;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.tree.BaseElement;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;
import org.xmpp.resultsetmanagement.ResultSet;
import org.xmpp.resultsetmanagement.ResultSetImpl;

public class ItemsGetTest extends IQTestHandler {

	private static final String NODE = "/user/pamela@denmark.lit/posts";
	private static final JID jid = new JID("juliet@shakespeare.lit");

	private IQ request;
	private ItemsGet itemsGet;
	private Element element;
	private BlockingQueue<Packet> queue = new LinkedBlockingQueue<Packet>();

	private ChannelManager channelManager;

	@Before
	public void setUp() throws Exception {

		queue = new LinkedBlockingQueue<Packet>();
		itemsGet = new ItemsGet(queue, channelManager);
		request = readStanzaAsIq("/iq/pubsub/items/request.stanza");
		element = new BaseElement("items");

		channelManager = mock(Mock.class);
		when(channelManager.isLocalNode(anyString())).thenReturn(true);
		itemsGet.setChannelManager(channelManager);
	}

	@Test
	public void testPassingAffiliationsAsElementNameReturnsTrue() {
		Assert.assertTrue(itemsGet.accept(element));
	}

	@Test
	public void testPassingNotCreateAsElementNameReturnsFalse() {
		Element element = new BaseElement("not-items");
		Assert.assertFalse(itemsGet.accept(element));
	}

	@Test
	public void testMissingNodeAttributeReturnsErrorStanza() throws Exception {
		itemsGet.process(element, jid, request, null);
		Packet response = queue.poll(100, TimeUnit.MILLISECONDS);

		PacketError error = response.getError();
		Assert.assertNotNull(error);
		Assert.assertEquals(PacketError.Type.modify, error.getType());
		Assert.assertEquals("nodeid-required",
				error.getApplicationConditionName());
	}

	@Test
	public void testExternalNodeReturnsExpectedStanzaEarly() throws Exception {

		when(channelManager.isLocalNode(anyString())).thenReturn(false);

		element.addAttribute("node", "/user/user@remote-server.com/posts");

		itemsGet.process(element, jid, request, null);
		Packet response = queue.poll(100, TimeUnit.MILLISECONDS);

		Element element = response.getElement();

		Assert.assertEquals(IQ.Type.get.toString(),
				element.attributeValue("type"));
		Assert.assertEquals("remote-server.com", response.getTo().getDomain());

		Assert.assertEquals(element.element("pubsub").element("actor")
				.getText(), response.getFrom().toBareJID());
	}

	@Test
	public void testNodeWhichDoesntExistReturnsNotFoundStanza()
			throws Exception {

		when(channelManager.nodeExists(NODE)).thenReturn(false);
		element.addAttribute("node", NODE);

		itemsGet.process(element, jid, request, null);
		Packet response = queue.poll(100, TimeUnit.MILLISECONDS);

		PacketError error = response.getError();
		Assert.assertNotNull(error);
		Assert.assertEquals(PacketError.Type.cancel, error.getType());
		Assert.assertEquals(PacketError.Condition.item_not_found,
				error.getCondition());
	}

	@Test
	public void testNodeStoreExceptionReturnsInternalServerErrorStanza()
			throws Exception {
		element.addAttribute("node", NODE);

		when(channelManager.nodeExists(NODE)).thenThrow(
				new NodeStoreException());

		itemsGet.process(element, jid, request, null);
		Packet response = queue.poll(100, TimeUnit.MILLISECONDS);

		PacketError error = response.getError();
		Assert.assertNotNull(error);
		Assert.assertEquals(PacketError.Type.wait, error.getType());
		Assert.assertEquals(PacketError.Condition.internal_server_error,
				error.getCondition());
	}

	@Test
	public void testSubscriptionIncompatibleWithItemRetrievalReturnsExpectedStanza()
			throws Exception {

		element.addAttribute("node", NODE);

		when(channelManager.nodeExists(NODE)).thenReturn(true);
		when(channelManager.getUserSubscription(NODE, jid)).thenReturn(null);

		NodeViewAcl nodeViewAclMock = mock(NodeViewAcl.class);
		doReturn(false).when(nodeViewAclMock).canViewNode(anyString(),
				any(Affiliations.class), any(Subscriptions.class),
				any(AccessModels.class));
		NodeAclRefuseReason refusalReason = new NodeAclRefuseReason(
				PacketError.Type.auth, PacketError.Condition.forbidden,
				"pending-subscription");
		when(nodeViewAclMock.getReason()).thenReturn(refusalReason);
		itemsGet.setNodeViewAcl(nodeViewAclMock);

		itemsGet.process(element, jid, request, null);
		Packet response = queue.poll(100, TimeUnit.MILLISECONDS);

		PacketError error = response.getError();
		Assert.assertNotNull(error);
		Assert.assertEquals(PacketError.Type.auth, error.getType());
		Assert.assertEquals(PacketError.Condition.forbidden,
				error.getCondition());
		Assert.assertEquals("pending-subscription",
				error.getApplicationConditionName());
	}

	@Test
	public void testStandardNodeWithNoItemsReturnsNoItems() throws Exception {

		AccessModels accessModel = AccessModels.authorize;

		element.addAttribute("node", NODE);

		NodeSubscriptionImpl subscription = Mockito
				.mock(NodeSubscriptionImpl.class);
		NodeAffiliationImpl affiliation = Mockito
				.mock(NodeAffiliationImpl.class);
		when(affiliation.getAffiliation()).thenReturn(Affiliations.member);
		when(subscription.getSubscription()).thenReturn(
				Subscriptions.subscribed);
		when(channelManager.getUserSubscription(NODE, jid)).thenReturn(
				subscription);
		when(channelManager.getUserAffiliation(NODE, jid)).thenReturn(
				affiliation);

		when(channelManager.getNodeItems(anyString())).thenReturn(
				new ResultSetImpl<NodeItem>(new ArrayList<NodeItem>()));
		when(channelManager.nodeExists(NODE)).thenReturn(true);

		NodeViewAcl nodeViewAclMock = mock(NodeViewAcl.class);
		doReturn(true).when(nodeViewAclMock).canViewNode(anyString(),
				any(Affiliations.class), any(Subscriptions.class),
				any(AccessModels.class));
		itemsGet.setNodeViewAcl(nodeViewAclMock);

		itemsGet.process(element, jid, request, null);
		Packet response = queue.poll(100, TimeUnit.MILLISECONDS);
		Element element = response.getElement();

		Assert.assertEquals(IQ.Type.result.toString(),
				element.attributeValue("type"));
		Assert.assertEquals(NODE, element.element("pubsub").element("items")
				.attributeValue("node"));
		Assert.assertNull(element.element("pubsub").element("items")
				.element("item"));
	}

	@Test
	public void testSubscriptionsNodeWithNoItemsReturnsNoItems()
			throws Exception {

		String node = NODE.replace("posts", "subscriptions");
		element.addAttribute("node", node);

		NodeSubscriptionImpl subscription = Mockito
				.mock(NodeSubscriptionImpl.class);
		NodeAffiliationImpl affiliation = Mockito
				.mock(NodeAffiliationImpl.class);
		when(affiliation.getAffiliation()).thenReturn(Affiliations.member);
		when(subscription.getSubscription()).thenReturn(
				Subscriptions.subscribed);
		when(channelManager.getUserSubscription(node, jid)).thenReturn(
				subscription);
		when(channelManager.getUserAffiliation(node, jid)).thenReturn(
				affiliation);

		when(channelManager.getNodeSubscriptions(anyString())).thenReturn(
				new ResultSetImpl<NodeSubscription>(
						new ArrayList<NodeSubscription>()));
		when(channelManager.nodeExists(node)).thenReturn(true);

		NodeViewAcl nodeViewAclMock = mock(NodeViewAcl.class);
		doReturn(true).when(nodeViewAclMock).canViewNode(anyString(),
				any(Affiliations.class), any(Subscriptions.class),
				any(AccessModels.class));
		itemsGet.setNodeViewAcl(nodeViewAclMock);

		itemsGet.process(element, jid, request, null);
		Packet response = queue.poll(100, TimeUnit.MILLISECONDS);
		Element element = response.getElement();

		Assert.assertEquals(IQ.Type.result.toString(),
				element.attributeValue("type"));
		Assert.assertEquals(node, element.element("pubsub").element("items")
				.attributeValue("node"));
		Assert.assertNull(element.element("pubsub").element("items")
				.element("item"));
	}

	@Test
	public void testUnparsableNodeEntryIsIgnoredInItemsResponse()
			throws Exception {
		AccessModels accessModel = AccessModels.authorize;

		element.addAttribute("node", NODE);

		NodeSubscriptionImpl subscription = Mockito
				.mock(NodeSubscriptionImpl.class);
		NodeAffiliationImpl affiliation = Mockito
				.mock(NodeAffiliationImpl.class);
		when(affiliation.getAffiliation()).thenReturn(Affiliations.member);
		when(subscription.getSubscription()).thenReturn(
				Subscriptions.subscribed);
		when(channelManager.getUserSubscription(NODE, jid)).thenReturn(
				subscription);
		when(channelManager.getUserAffiliation(NODE, jid)).thenReturn(
				affiliation);

		NodeItem item = mock(NodeItem.class);
		when(item.getId()).thenReturn("id");
		when(item.getUID()).thenReturn("id");
		when(item.getNodeId()).thenReturn(NODE);
		when(item.getPayload()).thenReturn("<entry>entry <text><entry>");

		ArrayList<NodeItem> items = new ArrayList<NodeItem>();
		items.add(item);
		ResultSet<NodeItem> itemList = new ResultSetImpl<NodeItem>(items);
		doReturn(itemList).when(channelManager).getNodeItems(anyString());

		when(channelManager.nodeExists(NODE)).thenReturn(true);

		NodeViewAcl nodeViewAclMock = mock(NodeViewAcl.class);
		doReturn(true).when(nodeViewAclMock).canViewNode(anyString(),
				any(Affiliations.class), any(Subscriptions.class),
				any(AccessModels.class));
		itemsGet.setNodeViewAcl(nodeViewAclMock);

		itemsGet.process(element, jid, request, null);
		Packet response = queue.poll(100, TimeUnit.MILLISECONDS);
		Element element = response.getElement();

		Assert.assertEquals(IQ.Type.result.toString(),
				element.attributeValue("type"));
		Assert.assertEquals(NODE, element.element("pubsub").element("items")
				.attributeValue("node"));
		Assert.assertEquals(0, element.element("pubsub").element("items")
				.nodeCount());
	}

	@Test
	public void testPostsNodeReturnsItemsAsExpected() throws Exception {
		AccessModels accessModel = AccessModels.authorize;

		element.addAttribute("node", NODE);

		NodeSubscriptionImpl subscription = Mockito
				.mock(NodeSubscriptionImpl.class);
		NodeAffiliationImpl affiliation = Mockito
				.mock(NodeAffiliationImpl.class);
		when(affiliation.getAffiliation()).thenReturn(Affiliations.member);
		when(subscription.getSubscription()).thenReturn(
				Subscriptions.subscribed);
		when(channelManager.getUserSubscription(NODE, jid)).thenReturn(
				subscription);
		when(channelManager.getUserAffiliation(NODE, jid)).thenReturn(
				affiliation);

		NodeItem item = mock(NodeItem.class);
		when(item.getId()).thenReturn("id");
		when(item.getUID()).thenReturn("id");
		when(item.getNodeId()).thenReturn(NODE);
		when(item.getPayload()).thenReturn("<entry>entry text</entry>");

		NodeItem item2 = mock(NodeItem.class);
		when(item2.getId()).thenReturn("id2");
		when(item2.getUID()).thenReturn("id2");
		when(item2.getNodeId()).thenReturn(NODE);
		when(item2.getPayload()).thenReturn("<entry>entry 2 text</entry>");

		ArrayList<NodeItem> items = new ArrayList<NodeItem>();
		items.add(item);
		items.add(item2);
		ResultSet<NodeItem> itemList = new ResultSetImpl<NodeItem>(items);
		doReturn(itemList).when(channelManager).getNodeItems(anyString());

		when(channelManager.nodeExists(NODE)).thenReturn(true);

		NodeViewAcl nodeViewAclMock = mock(NodeViewAcl.class);
		doReturn(true).when(nodeViewAclMock).canViewNode(anyString(),
				any(Affiliations.class), any(Subscriptions.class),
				any(AccessModels.class));
		itemsGet.setNodeViewAcl(nodeViewAclMock);

		itemsGet.process(element, jid, request, null);
		Packet response = queue.poll(100, TimeUnit.MILLISECONDS);
		Element element = response.getElement();

		Assert.assertEquals(IQ.Type.result.toString(),
				element.attributeValue("type"));
		Assert.assertEquals(NODE, element.element("pubsub").element("items")
				.attributeValue("node"));

		Assert.assertEquals(2, element.element("pubsub").element("items")
				.nodeCount());
		Assert.assertEquals("id", element.element("pubsub").element("items")
				.element("item").attributeValue("id"));
		Assert.assertEquals("entry text",
				element.element("pubsub").element("items").element("item")
						.elementText("entry"));
	}

	@Test
	public void testSubscriberThatHasNoSubscribersDoesNotCauseError()
			throws Exception {
		AccessModels accessModel = AccessModels.authorize;
		String node = NODE.replace("posts", "subscriptions");

		element.addAttribute("node", node);

		NodeSubscriptionImpl subscription = Mockito
				.mock(NodeSubscriptionImpl.class);
		NodeAffiliationImpl affiliation = Mockito
				.mock(NodeAffiliationImpl.class);
		when(affiliation.getAffiliation()).thenReturn(Affiliations.member);
		when(subscription.getSubscription()).thenReturn(
				Subscriptions.subscribed);
		when(channelManager.getUserSubscription(node, jid)).thenReturn(
				subscription);
		when(channelManager.getUserAffiliation(node, jid)).thenReturn(
				affiliation);

		NodeAffiliationImpl itemAffiliation = Mockito
				.mock(NodeAffiliationImpl.class);
		when(itemAffiliation.getAffiliation()).thenReturn(Affiliations.member);

		NodeSubscriptionImpl itemSubscription1 = Mockito
				.mock(NodeSubscriptionImpl.class);
		when(itemSubscription1.getSubscription()).thenReturn(
				Subscriptions.subscribed);
		when(itemSubscription1.getUser()).thenReturn(jid);
		when(itemSubscription1.getNodeId()).thenReturn(node);
		when(itemSubscription1.getUID()).thenReturn(jid.toString());

		JID jid2 = new JID("mercutio@shakespeare.lit");
		NodeSubscriptionImpl itemSubscription2 = Mockito
				.mock(NodeSubscriptionImpl.class);
		when(itemSubscription2.getSubscription()).thenReturn(
				Subscriptions.subscribed);
		when(itemSubscription2.getUser()).thenReturn(jid2);
		when(itemSubscription2.getNodeId()).thenReturn(node);
		when(itemSubscription2.getUID()).thenReturn(jid2.toString());

		JID jid3 = new JID("titania@shakespeare.lit");
		NodeSubscriptionImpl itemSubscription3 = Mockito
				.mock(NodeSubscriptionImpl.class);
		when(itemSubscription3.getSubscription()).thenReturn(
				Subscriptions.subscribed);
		when(itemSubscription3.getUser()).thenReturn(jid3);
		when(itemSubscription3.getNodeId()).thenReturn(node);
		when(itemSubscription3.getUID()).thenReturn(jid3.toString());

		ArrayList<NodeSubscription> items = new ArrayList<NodeSubscription>();
		items.add(itemSubscription1);
		items.add(itemSubscription2);
		items.add(itemSubscription3);
		doReturn(new ResultSetImpl<NodeSubscription>(items)).when(
				channelManager).getNodeSubscriptions(node);

		doReturn(null).when(channelManager).getUserAffiliation(node,
				new JID("pamela@denmark.lit"));

		when(channelManager.nodeExists(node)).thenReturn(true);

		NodeViewAcl nodeViewAclMock = mock(NodeViewAcl.class);
		doReturn(true).when(nodeViewAclMock).canViewNode(anyString(),
				any(Affiliations.class), any(Subscriptions.class),
				any(AccessModels.class));
		itemsGet.setNodeViewAcl(nodeViewAclMock);

		itemsGet.process(element, jid, request, null);
		Packet response = queue.poll(100, TimeUnit.MILLISECONDS);
		Element element = response.getElement();

		Assert.assertEquals(IQ.Type.result.toString(),
				element.attributeValue("type"));
		Assert.assertEquals(node, element.element("pubsub").element("items")
				.attributeValue("node"));

		Assert.assertEquals(3, element.element("pubsub").element("items")
				.nodeCount());
		Assert.assertEquals(0, element.element("pubsub").element("items")
				.element("item").element("query").elements().size());
	}

	@Test
	public void testSubscriptionsNodeReturnsItemsAsExpected() throws Exception {

		AccessModels accessModel = AccessModels.authorize;
		String node = NODE.replace("posts", "subscriptions");

		element.addAttribute("node", node);

		NodeSubscriptionImpl subscription = Mockito
				.mock(NodeSubscriptionImpl.class);
		NodeAffiliationImpl affiliation = Mockito
				.mock(NodeAffiliationImpl.class);
		when(affiliation.getAffiliation()).thenReturn(Affiliations.member);
		when(subscription.getSubscription()).thenReturn(
				Subscriptions.subscribed);
		when(channelManager.getUserSubscription(node, jid)).thenReturn(
				subscription);
		when(channelManager.getUserAffiliation(anyString(), any(JID.class)))
				.thenReturn(affiliation);

		NodeSubscriptionImpl itemSubscription = Mockito
				.mock(NodeSubscriptionImpl.class);
		NodeAffiliationImpl itemAffiliation = Mockito
				.mock(NodeAffiliationImpl.class);
		when(itemSubscription.getSubscription()).thenReturn(
				Subscriptions.subscribed);
		when(itemSubscription.getUser()).thenReturn(jid);
		when(itemSubscription.getUID()).thenReturn(jid.toString());
		when(itemSubscription.getNodeId()).thenReturn(node);
		ArrayList items = new ArrayList<NodeSubscriptionImpl>();
		items.add(itemSubscription);
		doReturn(new ResultSetImpl<NodeSubscription>(items)).when(
				channelManager).getNodeSubscriptions(node);

		NodeSubscriptionImpl childItemSubscription = Mockito
				.mock(NodeSubscriptionImpl.class);
		NodeAffiliationImpl childItemAffiliation = Mockito
				.mock(NodeAffiliationImpl.class);
		when(childItemAffiliation.getAffiliation()).thenReturn(
				Affiliations.member);
		when(childItemSubscription.getUser()).thenReturn(jid);
		when(childItemSubscription.getUID()).thenReturn(jid.toString());
		when(childItemSubscription.getNodeId()).thenReturn(
				"/user/juliet@shakespeare.lit/posts");
		when(childItemSubscription.getSubscription()).thenReturn(
				Subscriptions.subscribed);
		ArrayList<NodeSubscription> childItems = new ArrayList<NodeSubscription>();
		childItems.add(childItemSubscription);
		doReturn(new ResultSetImpl<NodeSubscription>(childItems)).when(
				channelManager).getUserSubscriptions(
				new JID("juliet@shakespeare.lit"));

		when(channelManager.nodeExists(node)).thenReturn(true);

		NodeViewAcl nodeViewAclMock = mock(NodeViewAcl.class);
		doReturn(true).when(nodeViewAclMock).canViewNode(anyString(),
				any(Affiliations.class), any(Subscriptions.class),
				any(AccessModels.class));
		itemsGet.setNodeViewAcl(nodeViewAclMock);

		itemsGet.process(element, jid, request, null);

		Packet response = queue.poll(100, TimeUnit.MILLISECONDS);
		Element element = response.getElement();

		Assert.assertEquals(IQ.Type.result.toString(),
				element.attributeValue("type"));
		Assert.assertEquals(node, element.element("pubsub").element("items")
				.attributeValue("node"));
		Assert.assertEquals(1, element.element("pubsub").element("items")
				.nodeCount());
		Assert.assertEquals(2, element.element("pubsub").element("items")
				.element("item").element("query").nodeCount());
		Assert.assertEquals(jid.toBareJID(),
				element.element("pubsub").element("items").element("item")
						.attributeValue("id"));
		Assert.assertEquals("/user/juliet@shakespeare.lit/posts",
				element.element("pubsub").element("items").element("item")
						.element("query").element("item")
						.attributeValue("node"));
		Assert.assertEquals(
				Affiliations.member.toString(),
				element.element("pubsub")
						.element("items")
						.element("item")
						.element("query")
						.element("item")
						.attributeValue(
								new QName("affiliation", new Namespace("ns1",
										JabberPubsub.NAMESPACE_URI))));
		Assert.assertEquals(
				Subscriptions.subscribed.toString(),
				element.element("pubsub")
						.element("items")
						.element("item")
						.element("query")
						.element("item")
						.attributeValue(
								new QName("subscription", new Namespace("ns2",
										JabberPubsub.NAMESPACE_URI))));
	}

	@Test
	public void testItemsGetWithRSMFirst10() throws Exception {
		createNodeWithXItems(NODE, 30);

		Element elRSM = DocumentHelper.createElement(QName.get("set",
				ResultSet.NAMESPACE_RESULT_SET_MANAGEMENT));

		elRSM.addElement("max").setText("10");

		itemsGet.process(element, jid, request, elRSM);
		Packet response = queue.poll(100, TimeUnit.MILLISECONDS);
		Element element = response.getElement();

		@SuppressWarnings("unchecked")
		List<Element> itemEls = element.element("pubsub").element("items")
				.elements("item");

		for (int i = 0; i < 10; ++i) {
			assertEquals("Incorrect item id at position " + i, "id" + i,
					itemEls.get(i).attributeValue("id"));
		}

		Element rsmElement = response.getElement().element("pubsub").element(
				QName.get("set", ResultSet.NAMESPACE_RESULT_SET_MANAGEMENT));

		assertEquals("Unexpected count value returned", "30",
				rsmElement.element("count").getText());
		assertEquals("Unexpected first value returned", "id0", rsmElement
				.element("first").getText());
		assertEquals("Unexpected last value returned", "id9", rsmElement
				.element("last").getText());
	}

	@Test
	public void testItemsGetWithRSMMax12Offset5() throws Exception {
		createNodeWithXItems(NODE, 30);

		Element elRSM = DocumentHelper.createElement(QName.get("set",
				ResultSet.NAMESPACE_RESULT_SET_MANAGEMENT));

		elRSM.addElement("max").setText("12");
		elRSM.addElement("after").setText("id4");

		itemsGet.process(element, jid, request, elRSM);
		Packet response = queue.poll(100, TimeUnit.MILLISECONDS);
		Element element = response.getElement();

		@SuppressWarnings("unchecked")
		List<Element> itemEls = element.element("pubsub").element("items")
				.elements("item");

		for (int i = 0; i < 12; ++i) {
			assertEquals("Incorrect item id at position " + i, "id" + (i + 5),
					itemEls.get(i).attributeValue("id"));
		}

		Element rsmElement = response.getElement().element("pubsub").element(
				QName.get("set", ResultSet.NAMESPACE_RESULT_SET_MANAGEMENT));

		assertEquals("Unexpected count value returned", "30",
				rsmElement.element("count").getText());
		assertEquals("Unexpected first value returned", "id5", rsmElement
				.element("first").getText());
		assertEquals("Unexpected last value returned", "id16", rsmElement
				.element("last").getText());
	}

	private void createNodeWithXItems(final String node, final int numItems)
			throws Exception {
		AccessModels accessModel = AccessModels.authorize;

		element.addAttribute("node", node);

		NodeSubscriptionImpl subscription = Mockito
				.mock(NodeSubscriptionImpl.class);
		NodeAffiliationImpl affiliation = Mockito
				.mock(NodeAffiliationImpl.class);
		when(affiliation.getAffiliation()).thenReturn(Affiliations.member);
		when(subscription.getSubscription()).thenReturn(
				Subscriptions.subscribed);
		when(channelManager.getUserSubscription(node, jid)).thenReturn(
				subscription);
		when(channelManager.getUserAffiliation(node, jid)).thenReturn(
				affiliation);

		// Create 30 node items
		ArrayList<NodeItem> items = new ArrayList<NodeItem>(numItems);

		for (int i = 0; i < numItems; ++i) {
			NodeItem item = mock(NodeItem.class);
			when(item.getId()).thenReturn("id" + i);
			when(item.getUID()).thenReturn("id" + i);
			when(item.getNodeId()).thenReturn(node);
			when(item.getPayload()).thenReturn(
					"<entry>entry text " + i + "</entry>");
			items.add(item);
		}

		ResultSet<NodeItem> itemList = new ResultSetImpl<NodeItem>(items);
		
		when(channelManager.getNodeItems(node)).thenReturn(itemList);

		when(channelManager.nodeExists(node)).thenReturn(true);

		NodeViewAcl nodeViewAclMock = mock(NodeViewAcl.class);
		when(
				nodeViewAclMock.canViewNode(eq(node), any(Affiliations.class),
						any(Subscriptions.class), any(AccessModels.class)))
				.thenReturn(true);
		itemsGet.setNodeViewAcl(nodeViewAclMock);
	}
}