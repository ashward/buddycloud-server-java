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

package org.buddycloud.channelserver.packetHandler.iq;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.buddycloud.channelserver.Configuration;
import org.buddycloud.channelserver.channel.TestHelper;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;

public class IQTestHandler
{
	private final static String CONFIGURATION_PROPERTIES = "src/test/resources/configuration.properties";
    public final static String  STANZA_PATH              = "src/test/resources/stanzas";
    public final static String  LOGGER_PROPERTIES        = "src/test/resources/log4j.properties";
    
    public static Properties readConf() 
        throws FileNotFoundException, IOException
    {
        Configuration conf = Configuration.getInstance();
        try {
            conf.load(new FileInputStream(CONFIGURATION_PROPERTIES));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return conf;
    }
    
    public static String readStanzaAsString(String stanzaPath) 
        throws IOException, DocumentException
    {
        String stanzaStr = IOUtils.toString(
                new FileInputStream(STANZA_PATH + stanzaPath));
        return stanzaStr.replaceAll("   ", "").replaceAll("\n", "");
    }
    
    public static IQ readStanzaAsIq(String stanzaPath)
        throws IOException, DocumentException
    {
        String stanzaStr = IOUtils.toString(
                new FileInputStream(STANZA_PATH + stanzaPath));
        return toIq(stanzaStr);
    }

    public static IQ toIq(String stanzaStr) throws DocumentException
    {
        return new IQ(parseXml(stanzaStr));
    }
    
    public static Element parseXml(String stanzaStr) throws DocumentException {
    	SAXReader xmlReader = new SAXReader();
        xmlReader.setMergeAdjacentText(true);
        xmlReader.setStringInternEnabled(true);
        xmlReader.setStripWhitespaceText(true);
        return xmlReader.read(new StringReader(stanzaStr)).getRootElement();
	}

	public void featureNotImplementedSuccess()
        throws IOException, InterruptedException, DocumentException
    {    
        IQ request = readStanzaAsIq("/iq/featureNotImplemented/request.stanza");
        String expectedReply = readStanzaAsString("/iq/featureNotImplemented/reply.stanza");
        
        TestHelper helper = new TestHelper();
        
        
        helper.getInQueue().put(request);
        
        IQ replyIQ = (IQ)helper.getOutQueue().poll(1000, TimeUnit.MILLISECONDS);
        
        Assert.assertNotNull(replyIQ);
        Assert.assertEquals(expectedReply, replyIQ.toXML());
    }
}
