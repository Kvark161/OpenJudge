package ru.openjudge.server.datalayer.domain;

import java.io.Serializable;

public class Contest implements Serializable {
	private static final long serialVersionUID = 2633150860373783463L;

	private long id;

	private String name;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
