package com.klevleev.eskimo.invoker;

import com.klevleev.eskimo.invoker.services.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.net.UnknownHostException;

@SpringBootApplication
public class Application {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) throws UnknownHostException {
		ApplicationContext context = SpringApplication.run(Application.class, args);
		new ConnectionThread(context.getBean(ServerService.class)).start();
	}

	private static class ConnectionThread extends Thread {

		private final ServerService serverService;

		ConnectionThread(ServerService serverService) {
			this.serverService = serverService;
		}

		@Override
		public void run() {
			//noinspection InfiniteLoopStatement
			while (true) {
				try {
					if (serverService.registerMe()) {
						logger.info("registered on the server");
					}
					sleep(60 * 1000);
				} catch (Throwable e) {
					logger.error("", e);
				}
			}
		}
	}

}
