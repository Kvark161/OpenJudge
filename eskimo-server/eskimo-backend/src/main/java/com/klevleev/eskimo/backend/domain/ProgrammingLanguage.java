package com.klevleev.eskimo.backend.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by Sokirkina Ekaterina on 29-Apr-2017.
 */
public class ProgrammingLanguage implements Serializable {

	@Getter @Setter
	private Long id;

	@Getter @Setter
	private String name;

	@Getter @Setter
	private String description;
}

