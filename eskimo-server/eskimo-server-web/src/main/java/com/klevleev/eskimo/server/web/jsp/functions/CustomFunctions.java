package com.klevleev.eskimo.server.web.jsp.functions;

import com.klevleev.eskimo.server.core.config.ApplicationSettings;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Locale;
import java.util.Map;

/**
 * Created by Ekaterina Sokirkina on 30-Aug-2016.
 */
public class CustomFunctions {

	public static String getName(Map<Locale, String> names, Locale locale){
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring/spring-core.xml");
		ApplicationSettings applicationSettings = (ApplicationSettings)applicationContext.getBean("applicationSettings");

		String name = names.get(locale);
		if (name == null){
			return applicationSettings.getDefaultLanguage();
		}
		return name;
	}
}
