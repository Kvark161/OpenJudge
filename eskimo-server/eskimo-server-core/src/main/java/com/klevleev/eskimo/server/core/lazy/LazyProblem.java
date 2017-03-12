package com.klevleev.eskimo.server.core.lazy;

import com.klevleev.eskimo.server.core.dao.ProblemDao;
import com.klevleev.eskimo.server.core.domain.Checker;
import com.klevleev.eskimo.server.core.domain.Problem;
import com.klevleev.eskimo.server.core.domain.Test;
import com.klevleev.eskimo.server.core.domain.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Sokirkina Ekaterina on 07-Feb-2017.
 */
@Component("lazyProblem")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LazyProblem extends Problem {

	private static final Logger logger = LoggerFactory.getLogger(LazyProblem.class);

	private final ProblemDao problemDao;

	private Set<String> gottenFields = new HashSet<>();

	@Autowired
	public LazyProblem(ProblemDao problemDao) {
		this.problemDao = problemDao;
	}

	@Override
	public void setNumberInContest(Long numberInContest) {
		super.setNumberInContest(numberInContest);
		gottenFields.add("numberInContest");
	}

	@Override
	public Long getNumberInContest() {
		if (!gottenFields.contains("numberInContest")){
			setProblemInfo(problemDao.getProblemInfo(super.getId()));
		}
		return super.getNumberInContest();
	}

	@Override
	public void setName(String name) {
		super.setName(name);
		gottenFields.add("name");
	}

	@Override
	public String getName() {
		if (!gottenFields.contains("name")){
			setProblemInfo(problemDao.getProblemInfo(super.getId()));
		}
		return super.getName();
	}

	@Override
	public void setTimeLimit(Long timeLimit) {
		super.setTimeLimit(timeLimit);
		gottenFields.add("timeLimit");
	}

	@Override
	public Long getTimeLimit() {
		if (!gottenFields.contains("timeLimit")){
			setProblemInfo(problemDao.getProblemInfo(super.getId()));
		}
		return super.getTimeLimit();
	}

	@Override
	public void setMemoryLimit(Long memoryLimit) {
		super.setMemoryLimit(memoryLimit);
		gottenFields.add("memoryLimit");
	}

	@Override
	public Long getMemoryLimit() {
		if (!gottenFields.contains("memoryLimit")){
			setProblemInfo(problemDao.getProblemInfo(super.getId()));
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
		logger.error("method getChecker() doesn't implemented");
		return null;
	}

	@Override
	public Validator getValidator() {
		logger.error("method getValidator() doesn't implemented");
		return null;
	}

	@Override
	public List<Test> getTests() {
		logger.error("method getTests() doesn't implemented");
		return null;
	}
}
