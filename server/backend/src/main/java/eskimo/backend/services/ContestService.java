package eskimo.backend.services;

import eskimo.backend.containers.*;
import eskimo.backend.dao.ContestDao;
import eskimo.backend.dao.ProblemDao;
import eskimo.backend.dao.StatementsDao;
import eskimo.backend.domain.Contest;
import eskimo.backend.domain.Statement;
import eskimo.backend.exceptions.AddEskimoEntityException;
import eskimo.backend.parsers.ProblemParserPolygonZip;
import eskimo.backend.storage.*;
import eskimo.backend.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContestService {

    private ContestDao contestDao;

    private ProblemDao problemDao;

    private StatementsDao statementsDao;

    private StorageService storageService;

    private FileUtils fileUtils;

    @Autowired
    public ContestService(ContestDao contestDao, ProblemDao problemDao, StatementsDao statementsDao, StorageService storageService, FileUtils fileUtils) {
        this.contestDao = contestDao;
        this.problemDao = problemDao;
        this.statementsDao = statementsDao;
        this.storageService = storageService;
        this.fileUtils = fileUtils;
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

    @Transactional
    public void addProblemFromZip(long contestId, File problemZip) {
        try (TemporaryFile unzippedFolder = new TemporaryFile(fileUtils.unzip(problemZip, "problem-zip-"))) {
            File[] files = unzippedFolder.getFile().listFiles();
            if (files == null || files.length != 1 || !files[0].isDirectory()) {
                throw new AddEskimoEntityException("Zip should contains exactly one folder");
            }
            ProblemContainer problemContainer = new ProblemParserPolygonZip(files[0]).parse();
            Long problemId = addProblem(contestId, problemContainer);
            addStatements(problemId, problemContainer);
        } catch (IOException e) {
            throw new AddEskimoEntityException("Exception occurred while unzipping the archive", e);
        }
    }

    private Long addProblem(long contestId, ProblemContainer problemContainer) {
        problemContainer.getProblem().setIndex(problemDao.getNextProblemIndex(contestId));
        problemContainer.getProblem().setContestId(contestId);
        Long id = problemDao.insertProblem(problemContainer.getProblem());
        List<StorageOrder> orders = prepareStorageOrdersToSave(problemContainer);
        storageService.executeOrders(orders);
        return id;
    }

    private void addStatements(long problemId, ProblemContainer problemContainer) {
        for (StatementContainer statementContainer : problemContainer.getStatements()) {
            Statement statement = statementContainer.getStatement();
            statement.setProblemId(problemId);
            statement.setLanguage(statementContainer.getLanguage());
            statementsDao.addStatements(statement);
        }
    }

    private List<StorageOrder> prepareStorageOrdersToSave(ProblemContainer container) {
        List<StorageOrder> orders = new ArrayList<>();
        long problemIndex = container.getProblem().getIndex();
        long contestId = container.getProblem().getContestId();
        orders.add(new StorageOrderCopyFile(container.getChecker(),
                storageService.getCheckerFile(contestId, problemIndex)));
        orders.add(new StorageOrderCopyFile(container.getValidator(),
                storageService.getValidatorFile(contestId, problemIndex)));
        for (SolutionContainer solution : container.getSolutions()) {
            File storageFile = storageService.getSolutionFile(contestId, problemIndex,
                    solution.getSolution().getName(), solution.getTag());
            orders.add(new StorageOrderCopyFile(solution.getSolution(), storageFile));
        }
        for (TestContainer test : container.getTests()) {
            orders.add(new StorageOrderCopyFile(test.getInput(),
                    storageService.getTestInputFile(contestId, problemIndex, test.getIndex())));
        }
        return orders;
    }
}
