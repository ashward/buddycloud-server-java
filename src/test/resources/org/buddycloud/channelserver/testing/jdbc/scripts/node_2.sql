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

INSERT INTO "nodes" ("node") VALUES ('users/node2@server1/posts');

INSERT INTO "node_config" ("node", "key", "value", "updated") VALUES ('users/node2@server1/posts', 'config1', 'Value of config1', now());
INSERT INTO "node_config" ("node", "key", "value", "updated") VALUES ('users/node2@server1/posts', 'config2', 'Value of config2', now());

INSERT INTO "affiliations" ("node", "user", "affiliation", "updated") VALUES ('users/node2@server1/posts', 'user2@server1', 'owner', now());
INSERT INTO "affiliations" ("node", "user", "affiliation", "updated") VALUES ('users/node2@server1/posts', 'user1@server1', 'publisher', now());

INSERT INTO "subscriptions" ("node", "user", "listener", "subscription", "updated") VALUES ('users/node2@server1/posts', 'user1@server1', 'user1@server1', 'subscribed', now());
INSERT INTO "subscriptions" ("node", "user", "listener", "subscription", "updated") VALUES ('users/node2@server1/posts', 'user1@server2', 'channels.server2', 'subscribed', now());
