package com.klevleev.eskimo.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


/**
 * Created by stepank on 21.03.2017.
 */
@SpringBootApplication
@EnableConfigurationProperties
public class BackendApp {

    public static void main(String... args) {
        SpringApplication.run(BackendApp.class, args);
    }

}
