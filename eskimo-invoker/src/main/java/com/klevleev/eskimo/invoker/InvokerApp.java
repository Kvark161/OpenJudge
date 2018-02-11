package com.klevleev.eskimo.invoker;

import com.klevleev.eskimo.invoker.services.ExecuteService;
import com.klevleev.eskimo.invoker.services.ExecuteServiceMac;
import com.klevleev.eskimo.invoker.utils.InvokerUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.UnknownHostException;

@SpringBootApplication
public class InvokerApp {

    private static final Logger logger = LoggerFactory.getLogger(InvokerApp.class);

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication.run(InvokerApp.class, args);
    }

    @Bean
    public ExecuteService getExecuteService(InvokerUtils invokerUtils) {
        if (SystemUtils.IS_OS_WINDOWS) {
            return new ExecuteServiceMac(invokerUtils);
        }
        logger.warn("Invoker can be ran only in testing mode on this operation system. Use Windows OS for production.");
        return new ExecuteServiceMac(invokerUtils);
    }

}
