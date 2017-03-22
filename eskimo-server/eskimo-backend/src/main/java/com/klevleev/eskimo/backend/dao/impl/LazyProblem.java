package com.klevleev.eskimo.backend.dao.impl;

import com.klevleev.eskimo.backend.dao.DaoFactory;
import com.klevleev.eskimo.backend.domain.Checker;
import com.klevleev.eskimo.backend.domain.Problem;
import com.klevleev.eskimo.backend.domain.Test;
import com.klevleev.eskimo.backend.domain.Validator;

import java.util.List;

/**
 * Created by Sokirkina Ekaterina on 07-Feb-2017.
 */
class LazyProblem extends Problem {

	private boolean isNumberInContestSet;
	private boolean isNameSet;
	private boolean isTimeLimitSet;
	private boolean isMemoryLimitSet;

	 LazyProblem(Long id) {
		setId(id);
	}

	@Override
	public void setNumberInContest(Long numberInContest) {
		super.setNumberInContest(numberInContest);
		isNumberInContestSet = true;
	}

	@Override
	public Long getNumberInContest() {
		if (!isNumberInContestSet){
			setProblemInfo(DaoFactory.getProblemDao().getProblemInfo(super.getId()));
		}
		return super.getNumberInContest();
	}

	@Override
	public void setName(String name) {
		super.setName(name);
		isNameSet = true;
	}

	@Override
	public String getName() {
		if (!isNameSet){
			setProblemInfo(DaoFactory.getProblemDao().getProblemInfo(super.getId()));
		}
		return super.getName();
	}

	@Override
	public void setTimeLimit(Long timeLimit) {
		super.setTimeLimit(timeLimit);
		isTimeLimitSet = true;
	}

	@Override
	public Long getTimeLimit() {
		if (!isTimeLimitSet){
			setProblemInfo(DaoFactory.getProblemDao().getProblemInfo(super.getId()));
		}
		return super.getTimeLimit();
	}

	@Override
	public void setMemoryLimit(Long memoryLimit) {
		super.setMemoryLimit(memoryLimit);
		isMemoryLimitSet = true;
	}

	@Override
	public Long getMemoryLimit() {
		if (!isMemoryLimitSet){
			setProblemInfo(DaoFactory.getProblemDao().getProblemInfo(super.getId()));
		}
		return super.getMemoryLimit();
	}

	private void setProblemInfo(Problem other){
		setId(other.getId());
		setNumberInContest(other.getNumberInContest());
		setName(other.getName());
		setTimeLimit(other.getTimeLimit());
		setMemoryLimit(other.getMemoryLimit());
	}

	@Override
	public Checker getChecker() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public Validator getValidator() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public List<Test> getTests() {
		throw new RuntimeException("not implemented");
	}
}
