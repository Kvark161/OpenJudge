package eskimo.backend.services;

import eskimo.backend.containers.ProblemContainer;
import eskimo.backend.containers.SolutionContainer;
import eskimo.backend.containers.StatementContainer;
import eskimo.backend.containers.TestContainer;
import eskimo.backend.dao.ProblemDao;
import eskimo.backend.dao.StatementsDao;
import eskimo.backend.entity.Problem;
import eskimo.backend.entity.Statement;
import eskimo.backend.exceptions.AddEskimoEntityException;
import eskimo.backend.judge.JudgeService;
import eskimo.backend.parsers.ProblemParserPolygonZip;
import eskimo.backend.rest.response.AnswersGenerationResponse;
import eskimo.backend.rest.response.ProblemInfoResponse;
import eskimo.backend.rest.response.StatementsResponse;
import eskimo.backend.storage.StorageOrder;
import eskimo.backend.storage.StorageOrderCopyFile;
import eskimo.backend.storage.StorageService;
import eskimo.backend.storage.TemporaryFile;
import eskimo.backend.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@Service
public class ProblemService {
    private static final Logger logger = LoggerFactory.getLogger(ProblemService.class);

    private final ProblemDao problemDao;
    private final StatementsDao statementsDao;
    private final StorageService storageService;
    private final FileUtils fileUtils;
    private final JudgeService judgeService;

    public ProblemService(ProblemDao problemDao, StatementsDao statementsDao, StorageService storageService, FileUtils fileUtils, JudgeService judgeService) {
        this.problemDao = problemDao;
        this.statementsDao = statementsDao;
        this.storageService = storageService;
        this.fileUtils = fileUtils;
        this.judgeService = judgeService;
    }

    public List<ProblemInfoResponse> getContestProblems(Long contestId) {
        List<Problem> contestProblems = problemDao.getContestProblems(contestId);
        Map<Long, String> problemNames = problemDao.getProblemNames(contestId);
        return contestProblems.stream()
                .map(problem -> {
                    ProblemInfoResponse response = new ProblemInfoResponse();
                    response.fillProblemFields(problem);
                    response.setName(problemNames.get(problem.getId()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    public List<AnswersGenerationResponse> getAnswerGenerationInfo(Long contestId) {
        List<Problem> contestProblems = problemDao.getContestProblems(contestId);
        return contestProblems.stream()
                .map(problem -> {
                    AnswersGenerationResponse response = new AnswersGenerationResponse();
                    response.setIndex(problem.getIndex());
                    response.setAnswersGenerationMessage(problem.getAnswersGenerationMessage());
                    response.setAnswersGenerationStatus(problem.getAnswersGenerationStatus());
                    return response;
                })
                .collect(Collectors.toList());
    }

    public Problem getProblemById(Long problemId) {
        return problemDao.getProblem(problemId);
    }

    public StatementsResponse getStatements(Long contestId, Integer problemIndex, String language) {
        StatementsResponse statementsResponse = new StatementsResponse();
        Problem contestProblem = problemDao.getContestProblem(contestId, problemIndex);
        statementsResponse.fillProblemFields(contestProblem);

        String resultLanguage = getExistingSuitableLanguage(contestProblem.getId(), language);
        if (resultLanguage == null) {
            statementsResponse.setError("There is no statements");
            logger.warn("There is no statements for problem {} in contest {}", problemIndex, contestId);
            return statementsResponse;
        }
        Statement statements = statementsDao.getStatements(contestProblem.getId(), resultLanguage);
        statementsResponse.fillStatementsFields(statements);
        return statementsResponse;
    }

    /**
     * If statements on requested language exists - returns requested language,
     * then try to get english statements (if it is not requested language),
     * else returns any language on which statements (for this problem) exists.
     */
    private String getExistingSuitableLanguage(Long problemId, String requestedLanguage) {
        List<String> languagesPriority = asList(requestedLanguage, "en", "ru");
        Set<String> supportedLanguages = new HashSet<>(statementsDao.getSupportedLanguages(problemId));
        if (supportedLanguages.isEmpty()) {
            return null;
        }
        String resultLanguage = null;
        for (String language : languagesPriority) {
            if (supportedLanguages.contains(language)) {
                resultLanguage = language;
                break;
            }
        }
        return Optional.ofNullable(resultLanguage).orElse(supportedLanguages.iterator().next());
    }

    @Transactional
    public Problem addProblemFromZip(long contestId, File problemZip) {
        try (TemporaryFile unzippedFolder = new TemporaryFile(fileUtils.unzip(problemZip, "problem-zip-"))) {
            File[] files = unzippedFolder.getFile().listFiles();
            if (files == null || files.length != 1 || !files[0].isDirectory()) {
                throw new AddEskimoEntityException("Zip should contains exactly one folder");
            }
            ProblemContainer problemContainer = new ProblemParserPolygonZip(files[0]).parse();
            return addProblem(contestId, problemContainer);
        } catch (IOException e) {
            throw new AddEskimoEntityException("Exception occurred while unzipping the archive", e);
        }
    }

    public void updateProblemStatuses(Problem problem) {
        problemDao.updateProblemStatuses(problem);
    }

    @Transactional
    public void generateAnswers(Long contestId, Integer problemIndex) {
        //todo problem doesn't exist
        Problem contestProblem = problemDao.getContestProblem(contestId, problemIndex);
        judgeService.generateAnswers(contestProblem);
    }

    private Problem addProblem(long contestId, ProblemContainer problemContainer) {
        problemContainer.getProblem().setIndex(problemDao.getNextProblemIndex(contestId));
        problemContainer.getProblem().setContestId(contestId);
        Long id = problemDao.insertProblem(problemContainer.getProblem());
        problemContainer.getProblem().setId(id);
        for (StatementContainer statementContainer: problemContainer.getStatements()) {
            addStatements(problemContainer.getProblem(), statementContainer);
        }
        List<StorageOrder> orders = prepareStorageOrdersToSave(problemContainer);
        storageService.executeOrders(orders);
        return problemContainer.getProblem();
    }

    private void addStatements(Problem problem, StatementContainer statementContainer) {
        Statement statement = statementContainer.getStatement();
        statement.setProblemId(problem.getId());
        statement.setLanguage(statementContainer.getLanguage());
        statementsDao.addStatements(statement);
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
            if (test.getAnswer() != null) {
                orders.add(new StorageOrderCopyFile(test.getAnswer(),
                        storageService.getTestAnswerFile(contestId, problemIndex, test.getIndex())));
            }
        }
        return orders;
    }

}
