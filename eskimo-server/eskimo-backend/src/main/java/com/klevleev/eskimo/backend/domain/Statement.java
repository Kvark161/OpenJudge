package com.klevleev.eskimo.backend.domain;

import lombok.Data;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Sokirkina Ekaterina on 03-Feb-2017.
 */
@Data
public class Statement implements Serializable {
	private static final long serialVersionUID = -5623307237343174281L;

	public static final String DEFAULT_LANGUAGE = "en";

	private Long id;
	private String language;
	private File file;
	private String format;

}
