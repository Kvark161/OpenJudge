package eskimo.backend.services;

import eskimo.backend.containers.*;
import eskimo.backend.dao.ContestDao;
import eskimo.backend.dao.ProblemDao;
import eskimo.backend.dao.StatementsDao;
import eskimo.backend.domain.Contest;
import eskimo.backend.domain.Statement;
import eskimo.backend.exceptions.CreateContestException;
import eskimo.backend.parsers.ProblemParserPolygonZip;
import eskimo.backend.storage.*;
import eskimo.backend.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ContestService {

    @Autowired
    private ContestDao contestDao;

    @Autowired
    private ProblemDao problemDao;

    @Autowired
    private StatementsDao statementDao;

    @Autowired
    private StorageService storageService;

    @Autowired
    private FileUtils fileUtils;

    @Transactional
    public Contest createContest(Contest contest) {
        ContestContainer ContestContainer = new ContestContainer();
        ContestContainer.setContest(contest);
        ContestContainer.setProblems(new ArrayList<>());
        ContestContainer.setStatements(new ArrayList<>());
        ContestContainer.setStatementsFiles(new ArrayList<>());
        createContest(ContestContainer);
        return contest;
    }

    private void createContest(ContestContainer contestContainer) {
        Long contestId = contestDao.insertContest(contestContainer.getContest());
        contestContainer.getContest().setId(contestId);

        statementDao.insertStatement(contestContainer.getStatements(), contestId);
        List<ProblemContainer> problems = contestContainer.getProblems();
        for (ProblemContainer problem : problems) {
            Long id = problemDao.insertProblem(problem.getProblem());
            problem.getProblem().setId(id);
        }
        List<StorageOrder> storageOrders = prepareStorageOrdersToSave(contestContainer);
        storageService.executeOrders(storageOrders);
    }

    private List<StorageOrder> prepareStorageOrdersToSave(ContestContainer contestContainer) {
        List<StorageOrder> orders = new ArrayList<>();
        Long contestId = contestContainer.getContest().getId();
        orders.add(new StorageOrderCreateFolder(storageService.getContestFolder(contestId)));

        List<Statement> statements = contestContainer.getStatements();
        List<File> statementsFiles = contestContainer.getStatementsFiles();
        for (int i = 0; i < statements.size(); ++i) {
            Statement statement = statements.get(i);
            File targetFile =
                    storageService.getStatementFile(contestId, statement.getLanguage(), statement.getFormat());
            orders.add(new StorageOrderCopyFile(statementsFiles.get(i), targetFile));
        }

        List<ProblemContainer> problems = contestContainer.getProblems();
        for (ProblemContainer problem : problems) {
            orders.addAll(prepareStorageOrdersToSave(problem));
        }
        return orders;
    }

    public Contest getContestById(Long contestId) {
        return contestDao.getContestInfo(contestId);
    }

    public List<Contest> getAllContests() {
        return contestDao.getAllContests();
    }

    public byte[] getStatements(Long contestId) throws IOException {
        FileInputStream statementsFileStream = new FileInputStream(storageService.getStatementFile(contestId));
        return IOUtils.toByteArray(statementsFileStream);
    }

    public void addProblemFromZip(long contestId, File problemZip) {
        try (TemporaryFile unzippedFolder = new TemporaryFile(fileUtils.unzip(problemZip, "problem-zip-"))) {
            File[] files = unzippedFolder.getFile().listFiles();
            if (files == null || files.length != 1 || !files[0].isDirectory()) {
                throw new CreateContestException("Zip should contains exactly one folder");
            }
            ProblemContainer problemContainer = new ProblemParserPolygonZip(files[0]).parse();
            addProblem(contestId, problemContainer);
        } catch (IOException e) {
            throw new CreateContestException("exception while unzip archive", e);
        }
    }

    @Transactional
    public void addProblem(long contestId, ProblemContainer problemContainer) {
        problemContainer.getProblem().setIndex(problemDao.getNextProblemIndex(contestId));
        problemContainer.getProblem().setContestId(contestId);
        problemDao.insertProblem(problemContainer.getProblem());
        prepareStorageOrdersToSave(problemContainer);
        List<StorageOrder> orders = prepareStorageOrdersToSave(problemContainer);
        storageService.executeOrders(orders);
    }

    private List<StorageOrder> prepareStorageOrdersToSave(ProblemContainer container) {
        List<StorageOrder> orders = new ArrayList<>();
        long problemIndex = container.getProblem().getIndex();
        long contestId = container.getProblem().getContestId();
        orders.add(new StorageOrderCopyFile(container.getChecker(), storageService.getCheckerFile(contestId, problemIndex)));
        orders.add(new StorageOrderCopyFile(container.getValidator(), storageService.getValidatorFile(contestId, problemIndex)));

        for (StatementContainer statement : container.getStatements()) {
            orders.add(new StorageOrderCopyFile(statement.getStatement(),
                    storageService.getStatementFile(contestId, problemIndex, statement.getLanguage(), "json")));
        }
        for (SolutionContainer solution : container.getSolutions()) {
            File storageFile = storageService.getSolutionFile(contestId, problemIndex, solution.getSolution().getName(), solution.getTag());
            orders.add(new StorageOrderCopyFile(solution.getSolution(), storageFile));
        }
        for (TestContainer test : container.getTests()) {
            orders.add(new StorageOrderCopyFile(test.getInput(),
                    storageService.getTestInputFile(contestId, problemIndex, test.getIndex())));
        }
        return orders;
    }
}
