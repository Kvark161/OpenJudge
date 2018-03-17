package eskimo.backend.dao;

import eskimo.backend.BaseTest;
import eskimo.backend.entity.Contest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ContestDaoTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(ContestDao.class);

    @Autowired
    private ContestDao contestDao;

    @Test
    public void getAllContests() {
        List<Contest> contestList = contestDao.getAllContests();
        System.out.println(contestList.size());
        assertThat(contestList).isNotNull();
    }

}
