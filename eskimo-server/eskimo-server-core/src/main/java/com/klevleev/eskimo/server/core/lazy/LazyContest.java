package com.klevleev.eskimo.server.core.lazy;

import com.klevleev.eskimo.server.core.dao.ContestDao;
import com.klevleev.eskimo.server.core.dao.ProblemDao;
import com.klevleev.eskimo.server.core.dao.StatementsDao;
import com.klevleev.eskimo.server.core.domain.Contest;
import com.klevleev.eskimo.server.core.domain.Problem;
import com.klevleev.eskimo.server.core.domain.Statements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Sokirkina Ekaterina on 06-Feb-2017.
 */
@Component("lazyContest")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LazyContest extends Contest {

	private static final Logger logger = LoggerFactory.getLogger(LazyContest.class);

	private final ContestDao contestDao;

	private final StatementsDao statementsDao;

	private final ProblemDao problemDao;

	private Set<String> gottenFields = new HashSet<>();

	@Autowired
	public LazyContest(ContestDao contestDao, StatementsDao statementsDao, ProblemDao problemDao) {
		this.contestDao = contestDao;
		this.statementsDao = statementsDao;
		this.problemDao = problemDao;
	}

	@Override
	public void setName(String name) {
		super.setName(name);
		gottenFields.add("name");
	}

	@Override
	public String getName() {
		if (!gottenFields.contains("name")) {
			setContestInfo(contestDao.getContestInfo(super.getId()));
		}
		return super.getName();
	}

	@Override
	public void setStartTime(LocalDateTime startTime) {
		super.setStartTime(startTime);
		gottenFields.add("startTime");
	}

	@Override
	public LocalDateTime getStartTime() {
		if (!gottenFields.contains("startTime")) {
			setContestInfo(contestDao.getContestInfo(super.getId()));
		}
		return super.getStartTime();
	}

	@Override
	public void setDuration(Integer duration) {
		super.setDuration(duration);
		gottenFields.add("duration");
	}

	@Override
	public Integer getDuration() {
		if (!gottenFields.contains("duration")) {
			setContestInfo(contestDao.getContestInfo(super.getId()));
		}
		return super.getDuration();
	}

	private void setContestInfo(Contest other){
		setId(other.getId());
		setName(other.getName());
		setStartTime(other.getStartTime());
		setDuration(other.getDuration());
	}

	@Override
	public void setStatements(List<Statements> statements) {
		super.setStatements(statements);
		gottenFields.add("statements");
	}

	@Override
	public List<Statements> getStatements() {
		if (!gottenFields.contains("statements")) {
			super.setStatements(statementsDao.getAllStatements(super.getId()));
		}
		return super.getStatements();
	}

	@Override
	public void setProblems(List<Problem> problems) {
		super.setProblems(problems);
		gottenFields.add("problems");
	}

	@Override
	public List<Problem> getProblems() {
		if (!gottenFields.add("problems")){
			super.setProblems(problemDao.getContestProblems(super.getId()));
		}
		return super.getProblems();
	}

}
