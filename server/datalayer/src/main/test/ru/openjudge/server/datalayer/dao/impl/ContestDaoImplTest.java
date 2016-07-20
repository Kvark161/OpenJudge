package ru.openjudge.server.datalayer.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.openjudge.server.datalayer.dao.ContestDao;
import ru.openjudge.server.datalayer.domain.Contest;

/**
 * Created by Stepan Klevleev on 20-Jul-16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/spring/root-spring.xml"})
public class ContestDaoImplTest {

	@Autowired
	private ContestDao contestDao;

	@Test
	public void insertContest() throws Exception {
		Contest contest = new Contest();
		contest.setName("custom contest name");
		contestDao.insertContest(contest);
		Assert.assertNotNull(contest.getId());
	}

}