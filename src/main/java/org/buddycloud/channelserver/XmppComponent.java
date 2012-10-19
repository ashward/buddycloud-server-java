package org.buddycloud.channelserver;

import org.jivesoftware.whack.ExternalComponentManager;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.configuration.PropertyConfigurator;
import org.xmpp.component.ComponentException;

public class XmppComponent {

	private static final String DATABASE_CONFIGURATION_FILE = "db.properties";
	
	private String hostname;
	private int socket;
	
	private String domainName;
	private String password;
	private Configuration conf;
	
	public XmppComponent(Configuration conf) {
	    setConf(conf);
		hostname = conf.getProperty("xmpp.host");
		socket = Integer.valueOf(conf.getProperty("xmpp.port"));
		domainName = conf.getProperty("xmpp.subdomain");
		password = conf.getProperty("xmpp.secretkey");

		try {
			PropertyConfigurator.configure(DATABASE_CONFIGURATION_FILE);
		} catch (ProxoolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void setConf(Configuration conf) {
		this.conf = conf;
	}
	
	public void run() throws ComponentException {
		ExternalComponentManager manager = new ExternalComponentManager(
		        this.hostname, this.socket);
		manager.setSecretKey(this.domainName, this.password);
		manager.addComponent(this.domainName, new ChannelsEngine(this.conf));
	}
}
