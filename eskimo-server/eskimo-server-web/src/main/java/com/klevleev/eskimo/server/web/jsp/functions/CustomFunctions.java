package com.klevleev.eskimo.server.web.jsp.functions;

import com.klevleev.eskimo.server.core.config.ApplicationSettings;
import com.klevleev.eskimo.server.core.utils.ApplicationContextProvider;

import java.util.Locale;
import java.util.Map;

/**
 * Created by Ekaterina Sokirkina on 30-Aug-2016.
 */
public class CustomFunctions {

	public static String getName(Map<Locale, String> names, Locale locale){
		ApplicationSettings applicationSettings =
				(ApplicationSettings) ApplicationContextProvider.getApplicationContext().getBean("applicationSettings");
		String name = names.get(locale);
		if (name == null){
			return applicationSettings.getDefaultLanguage();
		}
		return name;
	}
}
