package ru.openjudge.server.datalayer.dao.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.openjudge.server.datalayer.dao.ContestDao;
import ru.openjudge.server.datalayer.domain.Contest;

import java.util.List;

/**
 * Created by Stepan Klevleev on 20-Jul-16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/spring/root-spring.xml"})
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