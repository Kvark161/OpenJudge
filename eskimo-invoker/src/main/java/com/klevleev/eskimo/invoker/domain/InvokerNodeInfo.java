package com.klevleev.eskimo.invoker.domain;

import java.io.Serializable;

/**
 * Created by Stepan Klevleev on 16-Aug-16.
 */
public class InvokerNodeInfo implements Serializable {
	private static final long serialVersionUID = 3039858792073069558L;

	private String host;
	private int port;
	private int maxThreads;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getMaxThreads() {
		return maxThreads;
	}

	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}
}
