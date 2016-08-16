package com.klevleev.eskimo.invoker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class AppLauncher {
    
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(AppLauncher.class, args);
    }

}
