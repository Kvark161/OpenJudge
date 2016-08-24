package com.klevleev.eskimo.invoker.config;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Stepan Klevleev on 24-Aug-16.
 */
@Component("invokerSettings")
public class InvokerSettings {

	private static final String FILE_PROPERTIES_PATH = "invoker.properties";

	private static final String SERVER_PROTOCOL = "server.protocol";
	private static final String SERVER_HOST = "server.host";
	private static final String SERVER_PORT = "server.port";
	private static final String SERVER_PATH_REGISTER = "server.path.register";
	private static final String SERVER_PATH_GET_TEST_INPUT = "server.path.get.test.input";
	private static final String SERVER_PATH_GET_TEST_ANSWER = "server.path.get.test.answer";

	private static final String INVOKER_MAX_THREADS = "invoker.max.threads";
	private static final String INVOKER_TEMP_PATH = "invoker.temp.path";
	private static final String INVOKER_STORAGE_PATH = "invoker.storage.path";

	private final Properties properties = new Properties();

	@PostConstruct
	private void init() throws IOException {
		try (InputStream is = InvokerSettings.class.getClassLoader().getResourceAsStream(FILE_PROPERTIES_PATH)) {
			properties.load(is);
		}
		//noinspection ResultOfMethodCallIgnored
		getInvokerTempPath().mkdirs();
		//noinspection ResultOfMethodCallIgnored
		getInvokerStoragePath().mkdirs();
	}

	public String getPropery(String key) {
		return properties.getProperty(key);
	}

	public String getServerProtocol() {
		return getPropery(SERVER_PROTOCOL);
	}

	public String getServerHost() {
		return getPropery(SERVER_HOST);
	}

	public int getServerPort() {
		return Integer.valueOf(getPropery(SERVER_PORT));
	}

	public String getServerUrlRegister() {
		return getServerRootUrl() + getPropery(SERVER_PATH_REGISTER);
	}

	public String getServerUrlGetTestInput() {
		return getServerRootUrl() + getPropery(SERVER_PATH_GET_TEST_INPUT);
	}

	public String getServerUrlGetTestAnswer() {
		return getServerRootUrl() + getPropery(SERVER_PATH_GET_TEST_ANSWER);
	}

	public String getServerPathGetTestAnswer() {
		return getPropery(SERVER_PATH_GET_TEST_ANSWER);
	}

	public int getInvokerMaxThreads() {
		return Integer.valueOf(getPropery(INVOKER_MAX_THREADS));
	}

	public File getInvokerStoragePath() {
		return new File(getPropery(INVOKER_STORAGE_PATH));
	}

	public File getInvokerTempPath() {
		return new File(getPropery(INVOKER_TEMP_PATH));
	}

	public String getServerRootUrl() {
		return getServerProtocol() + "://" + getServerHost() + ":" + getServerPort() + "/";
	}

}
