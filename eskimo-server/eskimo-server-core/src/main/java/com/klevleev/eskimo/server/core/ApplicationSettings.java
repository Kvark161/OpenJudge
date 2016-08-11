package com.klevleev.eskimo.server.core;

import java.util.Properties;

/**
 * Created by Stepan Klevleev on 11-Aug-16.
 */
public class ApplicationSettings {

	private Properties properties;

	private String tempPath;

	public void init() {
		tempPath = properties.getProperty("eskimo.temp.path");
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String getTempPath() {
		return tempPath;
	}
}
