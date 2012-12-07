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

CREATE TABLE "nodes" ("node" TEXT NOT NULL PRIMARY KEY);
CREATE TABLE "node_config" ("node" TEXT NOT NULL REFERENCES "nodes" ("node"),
       	     		  "key" TEXT,
			  "value" TEXT,
			  "updated" TIMESTAMP,
			  PRIMARY KEY ("node", "key"));
CREATE TABLE "items" ("node" TEXT REFERENCES "nodes" ("node"),
       	     	    "id" TEXT NOT NULL,
		    "updated" TIMESTAMP,
		    "xml" TEXT,
		    PRIMARY KEY ("node", "id"));
CREATE INDEX "items_updated" ON "items" ("updated");
CREATE TABLE "subscriptions" ("node" TEXT REFERENCES "nodes" ("node"),
       	     		    "user" TEXT,
			    "listener" TEXT,
			    "subscription" TEXT,
 			    "updated" TIMESTAMP,
			    PRIMARY KEY ("node", "user"));
CREATE INDEX "subscriptions_updated" ON "subscriptions" ("updated");
CREATE TABLE "affiliations" ("node" TEXT REFERENCES "nodes" ("node"),
       	     		   "user" TEXT,
			   "affiliation" TEXT,
 			   "updated" TIMESTAMP,
			   PRIMARY KEY ("node", "user"));
CREATE INDEX "affiliations_updated" ON "affiliations" ("updated");

CREATE VIEW "open_nodes" AS
       SELECT DISTINCT "node" FROM "node_config"
       	      WHERE "key"='accessModel'
	        AND "value"='open';