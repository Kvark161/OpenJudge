package com.klevleev.eskimo.backend.domain;

import lombok.Data;

import java.io.File;

/**
 * Created by Sokirkina Ekaterina on 03-Feb-2017.
 */
@Data
public class Validator {

	private Long index;
	private File file;
	private String programmingLanguage;

}
