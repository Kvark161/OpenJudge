package com.klevleev.eskimo.server.core.domain;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Stepan Klevleev on 15-Aug-16.
 */
public class Problem implements Serializable {
	private static final long serialVersionUID = 4102520856376069141L;

	private Long id;
	private String index;
	private Map<Locale, String> names;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public Map<Locale, String> getNames() {
		return names;
	}

	public void setNames(Map<Locale, String> names) {
		this.names = names;
	}

	public String getName(Locale locale){
		return DomainUtils.getName(names, locale);
	}

}
