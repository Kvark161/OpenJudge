package com.klevleev.eskimo.backend.dao;

import com.klevleev.eskimo.backend.dao.DaoFactory;
import com.klevleev.eskimo.backend.domain.Contest;
import com.klevleev.eskimo.backend.domain.Problem;
import com.klevleev.eskimo.backend.domain.Statement;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Sokirkina Ekaterina on 06-Feb-2017.
 */
class LazyContest extends Contest {

	private boolean isNameSet;
	private boolean isStartTimeSet;
	private boolean isDurationSet;
	private boolean isStatementsSet;
	private boolean isProblemSet;

	LazyContest(Long id) {
		setId(id);
	}

	@Override
	public void setName(String name) {
		super.setName(name);
		isNameSet = true;
	}

	@Override
	public String getName() {
		if (!isNameSet) {
			setContestInfo(DaoFactory.getContestDao().getContestInfo(super.getId()));
		}
		return super.getName();
	}

	@Override
	public void setStartTime(LocalDateTime startTime) {
		super.setStartTime(startTime);
		isStartTimeSet = true;
	}

	@Override
	public LocalDateTime getStartTime() {
		if (!isStartTimeSet) {
			setContestInfo(DaoFactory.getContestDao().getContestInfo(super.getId()));
		}
		return super.getStartTime();
	}

	@Override
	public void setDuration(Integer duration) {
		super.setDuration(duration);
		isDurationSet = true;
	}

	@Override
	public Integer getDuration() {
		if (!isDurationSet) {
			setContestInfo(DaoFactory.getContestDao().getContestInfo(super.getId()));
		}
		return super.getDuration();
	}

	private void setContestInfo(Contest other) {
		setId(other.getId());
		setName(other.getName());
		setStartTime(other.getStartTime());
		setDuration(other.getDuration());
	}

	@Override
	public void setStatements(List<Statement> statements) {
		super.setStatements(statements);
		isStatementsSet = true;
	}

	@Override
	public List<Statement> getStatements() {
		if (!isStatementsSet) {
			super.setStatements(DaoFactory.getStatementDao().getAllStatements(super.getId()));
		}
		return super.getStatements();
	}

	@Override
	public void setProblems(List<Problem> problems) {
		super.setProblems(problems);
		isProblemSet = true;
	}

	@Override
	public List<Problem> getProblems() {
		if (!isProblemSet){
			super.setProblems(DaoFactory.getProblemDao().getContestProblems(super.getId()));
		}
		return super.getProblems();
	}

}
