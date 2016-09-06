package com.klevleev.eskimo.server.core.utils;

import com.klevleev.eskimo.server.core.config.ApplicationSettings;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by Ekaterina Sokirkina on 30-Aug-2016.
 */
@Component("springFactory")
public class SpringFactory implements ApplicationContextAware {

	private static ApplicationContext context;

	private static ApplicationSettings applicationSettings;

	public static ApplicationContext getApplicationContext() {
		return context;
	}

	public static ApplicationSettings getApplicationSettings(){
		if (applicationSettings == null)
			applicationSettings = context.getBean("applicationSettings", ApplicationSettings.class);
		return applicationSettings;
	}

	@Override
	public void setApplicationContext(ApplicationContext ac)
			throws BeansException {
		context = ac;
	}
}