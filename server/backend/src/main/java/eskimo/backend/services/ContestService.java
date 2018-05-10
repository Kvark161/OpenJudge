package eskimo.backend.services;

import eskimo.backend.dao.ContestDao;
import eskimo.backend.entity.Contest;
import eskimo.backend.storage.StorageOrder;
import eskimo.backend.storage.StorageOrderCreateFolder;
import eskimo.backend.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContestService {

    @Autowired
    private ContestDao contestDao;

    @Autowired
    private StorageService storageService;

    @Autowired
    private DashboardService dashboardService;


    @Transactional
    public Contest createContest(Contest contest) {
        Long contestId = contestDao.insertContest(contest);
        contest.setId(contestId);

        List<StorageOrder> storageOrders = getEmptyContestOrders(contest);
        storageService.executeOrders(storageOrders);
        return contest;
    }

    @Transactional
    public void editContest(Contest contest) {
        Contest oldContest = contestDao.getContestInfo(contest.getId());
        contestDao.editContest(contest);
        if (!oldContest.getStartTime().equals(contest.getStartTime())
                || !oldContest.getDuration().equals(contest.getDuration())
                || !oldContest.getScoringSystem().equals(contest.getScoringSystem())) {
            dashboardService.rebuild(contest.getId());
        }
    }

    private List<StorageOrder> getEmptyContestOrders(Contest contest) {
        List<StorageOrder> orders = new ArrayList<>();
        Long contestId = contest.getId();
        orders.add(new StorageOrderCreateFolder(storageService.getContestFolder(contestId)));
        return orders;
    }

    public Contest getContestById(Long contestId) {
        return contestDao.getContestInfo(contestId);
    }

    public List<Contest> getAllContests() {
        return contestDao.getAllContests();
    }

}
