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

package org.buddycloud.channelserver.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.buddycloud.channelserver.Configuration;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.db.jdbc.JDBCNodeStore;
import org.buddycloud.channelserver.db.jdbc.JDBCNodeStore.NodeStoreSQLDialect;
import org.buddycloud.channelserver.db.jdbc.dialect.Sql92NodeStoreDialect;
import org.logicalcobwebs.proxool.ProxoolException;

public class DefaultNodeStoreFactoryImpl implements NodeStoreFactory {

	private static final String CONFIGURATION_JDBC_DIALECT = "jdbc.dialect";

	private final NodeStoreSQLDialect dialect;

	public DefaultNodeStoreFactoryImpl(final Configuration conf)
			throws NodeStoreException {
		String dialectClass = conf.getProperty(
				CONFIGURATION_JDBC_DIALECT,
				Sql92NodeStoreDialect.class.getName());

		try {
			dialect = (NodeStoreSQLDialect) Class.forName(dialectClass)
					.newInstance();
		} catch (Exception e) {
			throw new NodeStoreException("Could not instantiate dialect class "
					+ dialectClass, e);
		}
	}

	@Override
	public NodeStore create() {

		Connection connection = null;
		try {
			connection = new JDBCConnectionFactory(null).getConnection();
			return new JDBCNodeStore(connection, dialect);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProxoolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
