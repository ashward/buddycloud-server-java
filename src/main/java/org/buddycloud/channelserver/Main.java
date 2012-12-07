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

package org.buddycloud.channelserver;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.xmpp.component.ComponentException;

public class Main
{
    private static Logger LOGGER = Logger.getLogger(Main.class);
	
    public static void main(String[] args) {
        
        PropertyConfigurator.configure("log4j.properties");
        Logger.getLogger(Main.class).setLevel(Level.DEBUG);
        
        LOGGER.info("Starting Buddycloud channel mockup version...");

    	Configuration conf = Configuration.getInstance(); 

        LOGGER.info("Connecting to '" + conf.getProperty("xmpp.host") + ":" 
            + conf.getProperty("xmpp.port") 
            + "' and trying to claim address '" 
            + conf.getProperty("xmpp.subdomain") + "'.");

        try {
            XmppComponent xmppComponent = new XmppComponent(conf);
            xmppComponent.run();
        } catch (ComponentException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        run();
    }

	private static void run()
	{
		while (true) {
		    try {
		        Thread.sleep(5000);
		    } catch (InterruptedException e) {
		        e.printStackTrace();
		    }
		}
	}
}