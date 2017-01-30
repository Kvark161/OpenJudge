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
	private Long timeLimit;
	private Long memoryLimit;
	private Long testsCount;

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

	public Long getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(Long timeLimit) {
		this.timeLimit = timeLimit;
	}

	public Long getMemoryLimit() {
		return memoryLimit;
	}

	public void setMemoryLimit(Long memoryLimit) {
		this.memoryLimit = memoryLimit;
	}

	public Long getTestsCount() {
		return testsCount;
	}

	public void setTestsCount(Long testsCount) {
		this.testsCount = testsCount;
	}
}
