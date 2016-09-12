package com.klevleev.eskimo.server.core.domain;

import java.io.Serializable;

/**
 * Created by Stepan Klevleev on 15-Aug-16.
 */
public class Problem implements Serializable {
	private static final long serialVersionUID = 4102520856376069141L;

	private Long id;
	private String index;
	private String name;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
