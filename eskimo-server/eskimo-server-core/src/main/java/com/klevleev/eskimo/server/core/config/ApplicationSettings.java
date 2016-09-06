package com.klevleev.eskimo.server.core.config;

import com.klevleev.eskimo.invoker.config.InvokerSettings;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Stepan Klevleev on 11-Aug-16.
 */
@Component("applicationSettings")
public class ApplicationSettings {

	private static final String FILE_PROPERTIES_PATH = "eskimo/server.properties";

	private static final String SERVER_STORAGE_PATH = "server.storage.path";
	private static final String SERVER_TEMP_PATH = "server.temp.path";
	private static final String SERVER_DEFAULT_LANGUAGE = "server.default.language";
	private static final String INVOKER_PROTOCOL = "invoker.protocol";

	private final Properties properties = new Properties();

	@PostConstruct
	private void init() throws IOException {
		try (InputStream is = InvokerSettings.class.getClassLoader().getResourceAsStream(FILE_PROPERTIES_PATH)) {
			properties.load(is);
		}
		//noinspection ResultOfMethodCallIgnored
		getStorageRoot().mkdirs();
		//noinspection ResultOfMethodCallIgnored
		getTempRoot().mkdirs();
	}

	public String getPropery(String key) {
		return properties.getProperty(key);
	}

	public File getStorageRoot() {
		return new File(getPropery(SERVER_STORAGE_PATH));
	}

	public File getTempRoot() {
		return new File(getPropery(SERVER_TEMP_PATH));
	}

	public String getDefaultLanguage() {
		return getPropery(SERVER_DEFAULT_LANGUAGE);
	}

	public String getInvokerProtocol() {
		return getPropery(INVOKER_PROTOCOL);
	}
}
