package com.klevleev.eskimo.backend.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Sokirkina Ekaterina on 29-Apr-2017.
 */
@Data
public class ProgrammingLanguage implements Serializable {

	private Long id;
	private String name;
	private String description;

}

