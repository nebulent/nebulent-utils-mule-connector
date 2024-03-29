/**
 * This file was automatically generated by the Mule Development Kit
 */
package com.nebulent.mule.utils;

import org.mule.RequestContext;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.ConnectionException;
import org.mule.api.MuleContext;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Processor;
import org.springframework.context.annotation.Configuration;


/**
 * Cloud Connector
 * 
 * @author MuleSoft, Inc.
 */
@Connector(name = "nebulent-utils", schemaVersion = "1.0-SNAPSHOT")
public class NebulentUtilsConnector {
	/**
	 * Configurable
	 */
	@Configurable
	private long period;

	/**
	 * Configurable
	 */
	@Configurable
	private long requestsLimit;

	/*
	 * @Configurable private String url;
	 */

	Throttler throttler = null;

	/**
	 * test scope
	 */
	int i = 0;

	/**
	 * Connect
	 * 
	 * @param username
	 *            A username
	 * @param password
	 *            A password
	 * @throws ConnectionException
	 */
	@Connect
	public void connect() throws ConnectionException {
	}

	/**
	 * Disconnect
	 */
	@Disconnect
	public void disconnect() {
		/*
		 * CODE FOR CLOSING A CONNECTION GOES IN HERE
		 */
	}

	/**
	 * Are we connected
	 */
	@ValidateConnection
	public boolean isConnected() {
		return true;
	}

	/**
	 * Are we connected
	 */
	@ConnectionIdentifier
	public String connectionId() {
		return "001";
	}

	/**
	 * Custom processor
	 * 
	 * {@sample.xml ../../../doc/Hello-connector.xml.sample
	 * nebulent-utils:throttle}
	 * 
	 * @param no params 
	 *            Content to be processed
	 * @return no return
	 * @throws Exception
	 */
	@Processor
	public void throttle() throws Exception {
		System.out.println("Request:" + i++);
		getThrottler().delay();
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public long getRequestsLimit() {
		return requestsLimit;
	}

	public void setRequestsLimit(long requestsLimit) {
		this.requestsLimit = requestsLimit;
	}

	public Throttler getThrottler() {
		return (this.throttler != null) ? this.throttler : (this.throttler = new Throttler(
				requestsLimit, period));
	}

	public void setThrottler(Throttler throttler) {
		this.throttler = throttler;
	}
}
