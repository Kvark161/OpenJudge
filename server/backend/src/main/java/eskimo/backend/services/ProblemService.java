package eskimo.backend.services;

import eskimo.backend.containers.ProblemContainer;
import eskimo.backend.containers.SolutionContainer;
import eskimo.backend.containers.StatementContainer;
import eskimo.backend.containers.TestContainer;
import eskimo.backend.dao.ProblemDao;
import eskimo.backend.dao.StatementsDao;
import eskimo.backend.entity.Problem;
import eskimo.backend.entity.Statement;
import eskimo.backend.entity.enums.GenerationStatus;
import eskimo.backend.exceptions.AddEskimoEntityException;
import eskimo.backend.judge.JudgeService;
import eskimo.backend.parsers.ProblemParserPolygonZip;
import eskimo.backend.rest.request.EditProblemRequest;
import eskimo.backend.rest.response.*;
import eskimo.backend.storage.StorageOrder;
import eskimo.backend.storage.StorageOrderCopyFile;
import eskimo.backend.storage.StorageService;
import eskimo.backend.storage.TemporaryFile;
import eskimo.backend.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    public List<AdminProblemsResponse> getAnswerGenerationInfo(Long contestId) {
        List<Problem> contestProblems = problemDao.getContestProblems(contestId);
        Map<Long, String> problemNames = problemDao.getProblemNames(contestId);
        return contestProblems.stream()
                .map(problem -> {
                    AdminProblemsResponse response = new AdminProblemsResponse();
                    response.fillProblemFields(problem);
                    response.setName(problemNames.get(problem.getId()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    public Problem getProblemById(Long problemId) {
        return problemDao.getProblem(problemId);
    }

    public ProblemForEditResponse getProblemForEdit(long contestId, int problemIndex) {
        Problem contestProblem = problemDao.getContestProblem(contestId, problemIndex);
        ProblemForEditResponse response = new ProblemForEditResponse();
        response.fillProblemFields(contestProblem);
        return response;
    }

    public ValidationResponse editProblem(long contestId, int problemIndex, EditProblemRequest editProblemRequest,
                                          MultipartFile checkerMultipartFile) {
        ValidationResponse validationResponse = validateProblemEdit(editProblemRequest);
        if (!validationResponse.getErrors().isEmpty()) {
            return validationResponse;
        }
        List<StorageOrder> filesToSave = new ArrayList<>();
        if (checkerMultipartFile != null) {
            try (TemporaryFile checkerFile =
                         new TemporaryFile(fileUtils.saveFile(checkerMultipartFile, "checker-", ".cpp"))) {
                filesToSave.add(new StorageOrderCopyFile(checkerFile.getFile(),
                        storageService.getCheckerSourceFile(contestId, problemIndex)));
                storageService.executeOrders(filesToSave);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        problemDao.editContestProblem(contestId, problemIndex, editProblemRequest);
        return validationResponse;
    }

    private ValidationResponse validateProblemEdit(EditProblemRequest editProblemRequest) {
        ValidationResponse validationResponse = new ValidationResponse();
        if (editProblemRequest.getTimeLimit() == null) {
            validationResponse.addError("timeLimit", "Time limit should not be null");
        } else {
            if (editProblemRequest.getTimeLimit() <= 0) {
                validationResponse.addError("timeLimit", "Time limit should not be less than 1");
            }
        }
        if (editProblemRequest.getMemoryLimit() == null) {
            validationResponse.addError("memoryLimit", "Memory limit should not be null");
        } else {
            if (editProblemRequest.getTimeLimit() <= 0) {
                validationResponse.addError("memoryLimit", "Memory limit should not be less than 1 byte");
            }
        }
        return validationResponse;
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

    public void updateAnswerGenerationProblemStatuses(Problem problem) {
        problemDao.updateAnswerGenerationProblemStatuses(problem);
    }

    public void updateCheckerCompilationStatuses(long problemId, GenerationStatus status, String message) {
        problemDao.updateCheckerCompilationProblemStatuses(problemId, status, message);
    }

    @Transactional
    public void generateAnswers(Long contestId, Integer problemIndex) {
        //todo problem doesn't exist
        Problem contestProblem = problemDao.getContestProblem(contestId, problemIndex);
        judgeService.generateAnswers(contestProblem);
        judgeService.compileChecker(contestProblem);
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
                storageService.getCheckerSourceFile(contestId, problemIndex)));
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
        if (container.getTestlib() != null) {
            orders.add(new StorageOrderCopyFile(container.getTestlib(), storageService.getTestlib(contestId, problemIndex)));
        }
        return orders;
    }

}
