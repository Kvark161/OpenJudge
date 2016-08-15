package com.klevleev.eskimo.server.core.dao.impl;

import com.klevleev.eskimo.server.core.dao.ContestDao;
import com.klevleev.eskimo.server.core.domain.Contest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by Stepan Klevleev on 20-Jul-16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/spring/spring-core.xml"})
public class ContestDaoImplTest {

	private static final Logger logger = LoggerFactory.getLogger(ContestDaoImpl.class);

	@Autowired
	private ContestDao contestDao;

	@Test
	public void getAllContests() {
		List<Contest> contestList = contestDao.getAllContests();
		System.out.println(contestList.size());
	}

}