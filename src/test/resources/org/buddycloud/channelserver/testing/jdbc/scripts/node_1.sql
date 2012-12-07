-- Buddycloud Channel Server
-- http://buddycloud.com/
--
-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements.  See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership.  The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License.  You may obtain a copy of the License at
-- 
-- http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied.  See the License for the
-- specific language governing permissions and limitations
-- under the License.

INSERT INTO "nodes" ("node") VALUES ('users/node1@server1/posts');

INSERT INTO "node_config" ("node", "key", "value", "updated") VALUES ('users/node1@server1/posts', 'config1', 'Value of config1', now());
INSERT INTO "node_config" ("node", "key", "value", "updated") VALUES ('users/node1@server1/posts', 'config2', 'Value of config2', now());

INSERT INTO "affiliations" ("node", "user", "affiliation", "updated") VALUES ('users/node1@server1/posts', 'user1@server1', 'owner', now());
INSERT INTO "affiliations" ("node", "user", "affiliation", "updated") VALUES ('users/node1@server1/posts', 'user2@server1', 'publisher', now());

INSERT INTO "subscriptions" ("node", "user", "listener", "subscription", "updated") VALUES ('users/node1@server1/posts', 'user1@server1', 'user1@server1', 'subscribed', now());
INSERT INTO "subscriptions" ("node", "user", "listener", "subscription", "updated") VALUES ('users/node1@server1/posts', 'user2@server1', 'user2@server1', 'subscribed', now());
INSERT INTO "subscriptions" ("node", "user", "listener", "subscription", "updated") VALUES ('users/node1@server1/posts', 'user1@server2', 'channels.server2', 'subscribed', now());
INSERT INTO "subscriptions" ("node", "user", "listener", "subscription", "updated") VALUES ('users/node1@server1/posts', 'user3@server2', 'channels.server2', 'subscribed', now());

-- The strange order of insertion of the items is deliberate
            
INSERT INTO "items" ("node", "id", "updated", "xml") VALUES ('users/node1@server1/posts', 'a5', TIMESTAMP '2010-01-08 11:45:12', '<entry xmlns="http://www.w3.org/2005/Atom" xmlns:activity="http://activitystrea.ms/spec/1.0/"> 
                <published>2010-01-08T11:45:12Z</published> 
                <author> 
                   <name>user2@server1</name> 
                   <jid xmlns="http://buddycloud.com/atom-elements-0">user2@server1</jid> 
                </author> 
                <content type="text">Test 5</content> 
                <geoloc xmlns="http://jabber.org/protocol/geoloc"> 
                   <text>London, England</text> 
                   <locality>London</locality> 
                   <country>England</country> 
                </geoloc> 
 
                <activity:verb>post</activity:verb>
                <activity:object>
                  <activity:object-type>note</activity:object-type>
                </activity:object>
             </entry>');

INSERT INTO "items" ("node", "id", "updated", "xml") VALUES ('users/node1@server1/posts', 'a2', TIMESTAMP '2010-01-06 22:32:12', '<entry xmlns="http://www.w3.org/2005/Atom" xmlns:activity="http://activitystrea.ms/spec/1.0/"> 
                <published>2010-01-06T22:32:12Z</published> 
                <author> 
                   <name>user1@server1</name> 
                   <jid xmlns="http://buddycloud.com/atom-elements-0">user1@server1</jid> 
                </author> 
                <content type="text">Test 2</content> 
                <geoloc xmlns="http://jabber.org/protocol/geoloc"> 
                   <text>Paris, France</text> 
                   <locality>Paris</locality> 
                   <country>France</country> 
                </geoloc> 
 
                <activity:verb>post</activity:verb>
                <activity:object>
                  <activity:object-type>note</activity:object-type>
                </activity:object>
             </entry>');
             
INSERT INTO "items" ("node", "id", "updated", "xml") VALUES ('users/node1@server1/posts', 'a4', TIMESTAMP '2010-01-08 10:14:54', '<entry xmlns="http://www.w3.org/2005/Atom" xmlns:activity="http://activitystrea.ms/spec/1.0/"> 
                <published>2010-01-08T10:14:54Z</published> 
                <author> 
                   <name>user1@server1</name> 
                   <jid xmlns="http://buddycloud.com/atom-elements-0">user1@server1</jid> 
                </author> 
                <content type="text">Test 4</content> 
                <geoloc xmlns="http://jabber.org/protocol/geoloc"> 
                   <text>London, England</text> 
                   <locality>London</locality> 
                   <country>England</country> 
                </geoloc> 
 
                <activity:verb>post</activity:verb>
                <activity:object>
                  <activity:object-type>note</activity:object-type>
                </activity:object>
             </entry>');

INSERT INTO "items" ("node", "id", "updated", "xml") VALUES ('users/node1@server1/posts', 'a3', TIMESTAMP '2010-01-07 13:33:34', '<entry xmlns="http://www.w3.org/2005/Atom" xmlns:activity="http://activitystrea.ms/spec/1.0/"> 
                <published>2010-01-07T13:33:34Z</published> 
                <author> 
                   <name>user2@server1</name> 
                   <jid xmlns="http://buddycloud.com/atom-elements-0">user2@server1</jid> 
                </author> 
                <content type="text">Test 3</content> 
                <geoloc xmlns="http://jabber.org/protocol/geoloc"> 
                   <text>London, England</text> 
                   <locality>London</locality> 
                   <country>England</country> 
                </geoloc> 
 
                <activity:verb>post</activity:verb>
                <activity:object>
                  <activity:object-type>note</activity:object-type>
                </activity:object>
             </entry>');
             
INSERT INTO "items" ("node", "id", "updated", "xml") VALUES ('users/node1@server1/posts', 'a1', TIMESTAMP '2010-01-06 21:41:32', '<entry xmlns="http://www.w3.org/2005/Atom" xmlns:activity="http://activitystrea.ms/spec/1.0/"> 
                <published>2010-01-06T21:41:32Z</published> 
                <author> 
                   <name>user1@server1</name> 
                   <jid xmlns="http://buddycloud.com/atom-elements-0">user1@server1</jid> 
                </author> 
                <content type="text">Test 1</content> 
                <geoloc xmlns="http://jabber.org/protocol/geoloc"> 
                   <text>Paris, France</text> 
                   <locality>Paris</locality> 
                   <country>France</country> 
                </geoloc> 
 
                <activity:verb>post</activity:verb>
                <activity:object>
                  <activity:object-type>note</activity:object-type>
                </activity:object>
             </entry>');
             