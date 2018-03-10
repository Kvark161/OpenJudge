package eskimo.backend.config;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.File;
import java.io.IOException;

/**
 * Adds eskimo.properties file (if exists) to spring property sources. Full path to file should be provided
 * by command line arguments as --config <config_name>
 */
public class EskimoContextInitializer implements
        ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final Logger logger = LoggerFactory.getLogger(EskimoContextInitializer.class);

    private static final String CONFIG_ARGUMENT_NAME = "config";

    private String[] args;

    public EskimoContextInitializer(String... args) {
        this.args = args;
    }

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        try {
            File eskimoPropertiesFile = getEskimoPropertiesFile();
            if (eskimoPropertiesFile == null) {
                logger.info("No external config provided");
                return;
            }
            configurableApplicationContext.getEnvironment().getPropertySources()
                    .addFirst(new ResourcePropertySource("file:" + eskimoPropertiesFile.getAbsolutePath()));
            logger.info("Provided config from {}", eskimoPropertiesFile.getAbsolutePath());
        } catch (IOException | NullPointerException e) {
            logger.warn("Cannot read custom eskimo properties", e);
        }
    }

    private File getEskimoPropertiesFile() {
        Options options = new Options();

        Option configOption = new Option("", CONFIG_ARGUMENT_NAME, true, "External configuration path");
        options.addOption(configOption);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            logger.error("Can't parse config option");
            return null;
        }

        String configPath = cmd.getOptionValue(CONFIG_ARGUMENT_NAME);
        if (configPath == null) {
            return null;
        }
        return new File(configPath);
    }
}
