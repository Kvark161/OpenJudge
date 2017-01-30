package com.klevleev.eskimo.server.core.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Contest implements Serializable {
	private static final long serialVersionUID = -7614541625538455702L;

	private long id;
	private String name;
	private LocalDateTime startTime;
	private Integer duration;

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

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
