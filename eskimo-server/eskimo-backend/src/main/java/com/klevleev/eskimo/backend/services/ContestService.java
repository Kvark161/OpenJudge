package com.klevleev.eskimo.backend.services;

import com.klevleev.eskimo.backend.containers.SavingContest;
import com.klevleev.eskimo.backend.containers.SavingProblem;
import com.klevleev.eskimo.backend.dao.ContestDao;
import com.klevleev.eskimo.backend.dao.ProblemDao;
import com.klevleev.eskimo.backend.dao.StatementsDao;
import com.klevleev.eskimo.backend.domain.Contest;
import com.klevleev.eskimo.backend.domain.Statement;
import com.klevleev.eskimo.backend.exceptions.CreateContestException;
import com.klevleev.eskimo.backend.parsers.FolderContestParserEskimo;
import com.klevleev.eskimo.backend.storage.*;
import com.klevleev.eskimo.backend.utils.FileUtils;
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

/**
 * Created by Stepan Klevleev on 25-Aug-16.
 */
@Service
@Slf4j
public class ContestService {

    private ContestDao contestDao;

    private ProblemDao problemDao;

    private StatementsDao statementDao;

    private StorageService storageService;

    private FileUtils fileUtils;

    @Autowired
    public ContestService(ContestDao contestDao,
                          ProblemDao problemDao,
                          StatementsDao statementDao,
                          StorageService storageService,
                          FileUtils fileUtils) {
        this.contestDao = contestDao;
        this.problemDao = problemDao;
        this.statementDao = statementDao;
        this.storageService = storageService;
        this.fileUtils = fileUtils;
    }

    @Transactional
    public Contest saveContestZip(File contestZip) {
        try (TemporaryFile unzippedFolder = new TemporaryFile(fileUtils.unzip(contestZip))) {
            File[] files = unzippedFolder.getFile().listFiles();
            if (files == null || files.length != 1 || !files[0].isDirectory()) {
                throw new CreateContestException("zip should contain only one folder");
            }
            File contestFolder = files[0];
            SavingContest savingContest = new FolderContestParserEskimo(contestFolder).parse();
            saveContest(savingContest);
            return savingContest.getContest();
        } catch (IOException e) {
            throw new CreateContestException("exception while unzip archive");
        }
    }

    private void saveContest(SavingContest savingContest) {
        Long contestId = contestDao.insertContest(savingContest.getContest());
        savingContest.getContest().setId(contestId);

        statementDao.insertStatement(savingContest.getStatements(), contestId);
        List<SavingProblem> problems = savingContest.getProblems();
        for (SavingProblem savingProblem : problems) {
            Long id = problemDao.insertProblem(savingProblem.getProblem(), contestId);
            savingProblem.getProblem().setId(id);
        }
        List<StorageOrder> storageOrders = prepareStorageOrdersToSave(savingContest);
        storageService.executeOrders(storageOrders);
    }

    private List<StorageOrder> prepareStorageOrdersToSave(SavingContest savingContest) {
        List<StorageOrder> orders = new ArrayList<>();
        Long contestId = savingContest.getContest().getId();
        orders.add(new StorageOrderCreateFolder(storageService.getContestFolder(contestId)));

        List<Statement> statements = savingContest.getStatements();
        List<File> statementsFiles = savingContest.getStatementsFiles();
        for (int i = 0; i < statements.size(); ++i) {
            Statement statement = statements.get(i);
            File targetFile =
                    storageService.getStatementFile(contestId, statement.getLanguage(), statement.getFormat());
            orders.add(new StorageOrderCopyFile(statementsFiles.get(i), targetFile));
        }

        List<SavingProblem> problems = savingContest.getProblems();
        for (SavingProblem problem : problems) {
            long problemId = problem.getProblem().getIndex();
            File checkerFile = problem.getChecker();
            orders.add(new StorageOrderCopyFile(checkerFile,
                    new File(storageService.getCheckerFolder(savingContest.getContest().getId(), problemId)
                            + File.separator + checkerFile.getName())));

            File validatorFile = problem.getValidator();
            orders.add(new StorageOrderCopyFile(validatorFile,
                    new File(storageService.getValidatorFolder(savingContest.getContest().getId(), problemId)
                            + File.separator + validatorFile.getName())));

            int testsCount = problem.getTestsInput().size();
            List<File> testsInput = problem.getTestsInput();
            List<File> testsAnswer = problem.getTestsAnswer();
            for (int i = 0; i < testsCount; ++i) {
                orders.add(new StorageOrderCopyFile(testsInput.get(i),
                        storageService.getTestInputFile(contestId, problem.getProblem().getIndex(), i + 1L)));
                orders.add(new StorageOrderCopyFile(testsAnswer.get(i),
                        storageService.getTestAnswerFile(contestId, problem.getProblem().getIndex(), i + 1L)));
            }
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
}
