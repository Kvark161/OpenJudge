package com.klevleev.eskimo.backend.dao.impl;

import com.klevleev.eskimo.backend.dao.DaoFactory;
import com.klevleev.eskimo.backend.domain.*;

import java.util.List;

/**
 * Created by Sokirkina Ekaterina on 07-Feb-2017.
 */
class LazyProblem extends Problem {

	private boolean isInfoSet;
	private boolean isCheckerSet;
	private boolean isValidatorSet;
	private boolean isTestsSet;
	private boolean isSolutionsSet;

	LazyProblem(Long id) {
		setId(id);
	}

	@Override
	public void setIndex(Long numberInContest) {
		checkProblemInfo();
		super.setIndex(numberInContest);
	}

	@Override
	public Long getIndex() {
		checkProblemInfo();
		return super.getIndex();
	}

	@Override
	public void setName(String name) {
		checkProblemInfo();
		super.setName(name);
	}

	@Override
	public String getName() {
		checkProblemInfo();
		return super.getName();
	}

	@Override
	public void setTimeLimit(Long timeLimit) {
		checkProblemInfo();
		super.setTimeLimit(timeLimit);
	}

	@Override
	public Long getTimeLimit() {
		checkProblemInfo();
		return super.getTimeLimit();
	}

	@Override
	public void setMemoryLimit(Long memoryLimit) {
		checkProblemInfo();
		super.setMemoryLimit(memoryLimit);
	}

	@Override
	public Long getMemoryLimit() {
		checkProblemInfo();
		return super.getMemoryLimit();
	}

	private void checkProblemInfo() {
		if (!isInfoSet) {
			setProblemInfo(DaoFactory.getProblemDao().getProblemInfo(getId()));
			isInfoSet = true;
		}
	}

	private void setProblemInfo(Problem other){
		super.setIndex(other.getIndex());
		super.setName(other.getName());
		super.setTimeLimit(other.getTimeLimit());
		super.setMemoryLimit(other.getMemoryLimit());
	}

	@Override
	public void setChecker(Checker checker) {
		isCheckerSet = true;
		super.setChecker(checker);
	}

	@Override
	public Checker getChecker() {
		if (!isCheckerSet) {
			throw new RuntimeException("not implemented");
		}
		return super.getChecker();
	}

	@Override
	public void setValidator(Validator validator) {
		isValidatorSet = true;
		super.setValidator(validator);
	}


	@Override
	public Validator getValidator() {
		if (!isValidatorSet) {
			throw new RuntimeException("not implemented");
		}
		return super.getValidator();
	}

	@Override
	public void setTests(List<Test> tests) {
		isTestsSet = true;
		super.setTests(tests);
	}

	@Override
	public List<Test> getTests() {
		if (!isTestsSet) {
			throw new RuntimeException("not implemented");
		}
		return super.getTests();
	}

	@Override
	public void setSolutions(List<Solution> solutions) {
		isSolutionsSet = true;
		super.setSolutions(solutions);
	}

	@Override
	public List<Solution> getSolutions() {
		if (!isSolutionsSet) {
			throw new RuntimeException("not implemented");
		}
		return super.getSolutions();
	}

}
