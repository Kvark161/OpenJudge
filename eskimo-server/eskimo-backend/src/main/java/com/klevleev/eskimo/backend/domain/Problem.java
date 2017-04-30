package com.klevleev.eskimo.backend.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Sokirkina Ekaterina on 06-Feb-2017.
 */
@Data
public class Problem implements Serializable {
	private static final long serialVersionUID = 304023522081325148L;

	private Long id;
	private Long index;
	private String name;
	private Long timeLimit;
	private Long memoryLimit;
	private Checker checker;
	private Validator validator;
	private List<Test> tests;
	private List<Solution> solutions;

}
