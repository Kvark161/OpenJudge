package eskimo.backend.services;

import eskimo.backend.dao.ContestDao;
import eskimo.backend.entity.Contest;
import eskimo.backend.storage.StorageOrder;
import eskimo.backend.storage.StorageOrderCreateFolder;
import eskimo.backend.storage.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContestService {

    private ContestDao contestDao;
    private StorageService storageService;

    public ContestService(ContestDao contestDao, StorageService storageService) {
        this.contestDao = contestDao;
        this.storageService = storageService;
    }

    @Transactional
    public Contest createContest(Contest contest) {
        Long contestId = contestDao.insertContest(contest);
        contest.setId(contestId);

        List<StorageOrder> storageOrders = getEmptyContestOrders(contest);
        storageService.executeOrders(storageOrders);
        return contest;
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
