package com.klevleev.eskimo.server.core.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Contest implements Serializable {
	private static final long serialVersionUID = -7614541625538455702L;

	private long id;
	private String name;
	private List<Problem> problems;
	private LocalDateTime startTime;
	private Integer durationInMinutes;

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public Integer getDurationInMinutes() {
		return durationInMinutes;
	}

	public void setDurationInMinutes(Integer durationInMinutes) {
		this.durationInMinutes = durationInMinutes;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Problem> getProblems() {
		return problems;
	}

	public void setProblems(List<Problem> problems) {
		this.problems = problems;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
