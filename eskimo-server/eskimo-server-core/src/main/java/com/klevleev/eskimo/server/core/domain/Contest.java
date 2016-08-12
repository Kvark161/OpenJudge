package com.klevleev.eskimo.server.core.domain;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

public class Contest implements Serializable {
	private static final long serialVersionUID = 2633150860373783463L;

	private long id;

	private Map<Locale, String> names;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Map<Locale, String> getNames() {
		return names;
	}

	public void setNames(Map<Locale, String> names) {
		this.names = names;
	}

	public String getName(Locale locale) {
		String name = names.get(new Locale(locale.getLanguage()));
		if (name != null) {
			return name;
		}
		return names.get(new Locale("en"));
	}
}
