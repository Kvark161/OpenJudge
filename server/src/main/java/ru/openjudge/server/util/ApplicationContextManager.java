package ru.openjudge.server.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ApplicationContextManager {
    private static ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    private ApplicationContextManager() {
    }
}
