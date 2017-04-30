package com.klevleev.eskimo.backend.domain;


import lombok.Data;

import java.io.File;
import java.io.Serializable;


/**
 * Created by Sokirkina Ekaterina on 03-Feb-2017.
 */
@Data
public class Checker implements Serializable {

	private Long id;
	private String programmingLanguage;
	private File file;

}
