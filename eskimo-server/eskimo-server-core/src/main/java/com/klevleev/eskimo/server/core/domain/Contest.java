package com.klevleev.eskimo.server.core.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Contest implements Serializable {
	private static final long serialVersionUID = -7614541625538455702L;

	private long id;
	private Map<Locale, String> names;
	private List<Problem> problems;

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

	public List<Problem> getProblems() {
		return problems;
	}

	public void setProblems(List<Problem> problems) {
		this.problems = problems;
	}
}
