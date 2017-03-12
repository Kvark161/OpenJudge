package com.klevleev.eskimo.server.core.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Sokirkina Ekaterina on 06-Feb-2017.
 */
public class Contest implements Serializable {
	private static final long serialVersionUID = -7614541625538455702L;

	@Getter	@Setter
	private Long id;

	@Getter @Setter
	private String name;

	@Getter @Setter
	private LocalDateTime startTime;

	@Getter @Setter
	private Integer duration;

	@Getter @Setter
	private List<Statements> statements;

	@Getter @Setter
	private List<Problem> problems;
}
