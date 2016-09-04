package com.klevleev.eskimo.server.core.domain;

import com.klevleev.eskimo.server.core.utils.SpringFactory;

import java.util.Locale;
import java.util.Map;

/**
 * Created by Sokirkina Ekaterina on 04-Sep-2016.
 */
class DomainUtils {

	/**
	 * @return Returns appropriate name or, if doesn't exist, name in default locale
	 */
	static String getName(Map<Locale, String> names, Locale locale){
		String name = names.get(locale);
		if (name == null){
			name = names.get(new Locale(SpringFactory.getApplicationSettings().getDefaultLanguage()));
		}
		return name;
	}
}
