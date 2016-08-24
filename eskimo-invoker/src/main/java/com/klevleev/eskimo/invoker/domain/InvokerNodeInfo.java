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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		InvokerNodeInfo that = (InvokerNodeInfo) o;

		return port == that.port && (host != null ? host.equals(that.host) : that.host == null);

	}

	@Override
	public int hashCode() {
		int result = host != null ? host.hashCode() : 0;
		result = 31 * result + port;
		return result;
	}
}
