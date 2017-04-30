package com.klevleev.eskimo.backend.domain;

import lombok.Data;

import java.io.File;

/**
 * Created by Sokirkina Ekaterina on 12-Mar-2017.
 */
@Data
public class Solution {

	private Long id;
	private File file;
	private String programmingLanguage;

}
