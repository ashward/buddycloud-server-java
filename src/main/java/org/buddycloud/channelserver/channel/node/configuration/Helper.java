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

package org.buddycloud.channelserver.channel.node.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.buddycloud.channelserver.channel.node.configuration.field.ConfigurationFieldException;
import org.buddycloud.channelserver.channel.node.configuration.field.Factory;
import org.buddycloud.channelserver.channel.node.configuration.field.Field;
import org.dom4j.Element;
import org.xmpp.forms.DataForm;
import org.xmpp.forms.FormField;
import org.xmpp.packet.IQ;

public class Helper
{
	protected HashMap<String, Field> elements;
	
	HashMap<String, Field> config;
	private Factory           fieldFactory;

	public  static final String FORM_TYPE               = "http://jabber.org/protocol/pubsub#node_config";
	private static final String ELEMENT_NOT_FOUND       = "Required XMPP element not found";

	private static final Logger LOGGER                  = Logger.getLogger(Helper.class);
	
    public void parse(IQ request) throws NodeConfigurationException
    {
        try {
            parseConfiguration(getConfigurationValues(request));
        } catch (NullPointerException e) {
        	LOGGER.debug(e.getStackTrace());
        	throw new NodeConfigurationException(ELEMENT_NOT_FOUND);
        } catch (ConfigurationFieldException e) {
        	LOGGER.debug(e.getStackTrace());
        	throw new NodeConfigurationException();
        }
    }

    private List<FormField> getConfigurationValues(IQ request)
    {
        Element element = request
        	.getElement()
        	.element("pubsub")
            .element("configure")
            .element("x");
        DataForm dataForm      = new DataForm(element);
        List<FormField> fields = dataForm.getFields();
        return fields;
    }

	private void parseConfiguration(List<FormField> configurationValues)
	{
        elements = new HashMap<String, Field>();
		if (0 == configurationValues.size()) {
			return;
		}
		Field field;
		for (FormField configurationValue : configurationValues) {
			field = getFieldFactory()
			    .create(configurationValue.getVariable(), configurationValue.getFirstValue());
			elements.put(
				field.getName(),
				field
		    );
		}
	}

	private Factory getFieldFactory()
	{
		if (null == fieldFactory) {
			fieldFactory = new Factory();
		}
		return fieldFactory;
	}
	
	public void setFieldFactory(Factory factory)
	{
		fieldFactory = factory;
	}

	public boolean isValid() 
	{
		for (Entry<String, Field> element : elements.entrySet()) {
			if (false == element.getValue().isValid()) {
				LOGGER.debug(
				    "Configuration field " + element.getValue().getName() 
				    + " is not valid with value " + element.getValue().getValue()
				);
				return false;
			}
		}
		return true;
	}
	
	public HashMap<String, String> getValues()
	{
		HashMap<String, String> data = new HashMap<String, String>();
		for (Entry<String, Field> element : elements.entrySet()) {
			String value = element.getValue().getValue();
			String key   = element.getValue().getName();
			LOGGER.trace("For '" + key + "' we are storing value '" + value + "'");
			data.put(key, value);
		}
		return data;
	}
}