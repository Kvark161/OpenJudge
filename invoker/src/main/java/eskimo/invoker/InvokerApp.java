package eskimo.invoker;

import eskimo.invoker.config.InvokerSettings;
import eskimo.invoker.services.ExecuteService;
import eskimo.invoker.services.ExecuteServiceMac;
import eskimo.invoker.services.ExecuteServiceWindows;
import eskimo.invoker.utils.InvokerUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InvokerApp {

    private static final Logger logger = LoggerFactory.getLogger(InvokerApp.class);

    public static void main(String[] args) {
        SpringApplication.run(InvokerApp.class, args);
    }

    @Bean
    public ExecuteService getExecuteService(InvokerUtils invokerUtils, InvokerSettings invokerSettings) {
        if (SystemUtils.IS_OS_WINDOWS) {
            return new ExecuteServiceWindows(invokerUtils, invokerSettings);
        }
        logger.warn("Invoker can be ran only in testing mode on this operation system. Use Windows OS for production.");
        return new ExecuteServiceMac(invokerUtils);
    }

}
