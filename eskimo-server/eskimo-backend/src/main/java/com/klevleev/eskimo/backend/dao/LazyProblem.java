package com.klevleev.eskimo.backend.dao;

import com.klevleev.eskimo.backend.domain.Problem;

/**
 * Created by Sokirkina Ekaterina on 07-Feb-2017.
 */
class LazyProblem extends Problem {

	private boolean isInfoSet;

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

}
