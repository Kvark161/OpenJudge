package eskimo.backend.judge.jobs;

import eskimo.backend.entity.Problem;
import eskimo.backend.entity.ProgrammingLanguage;
import eskimo.backend.entity.enums.GenerationStatus;
import eskimo.backend.services.InvokerService;
import eskimo.backend.services.ProblemService;
import eskimo.backend.services.ProgrammingLanguageService;
import eskimo.backend.storage.StorageOrder;
import eskimo.backend.storage.StorageOrderCreateFile;
import eskimo.backend.storage.StorageService;
import eskimo.invoker.entity.CompilationResult;
import eskimo.invoker.entity.TestLazyParams;
import eskimo.invoker.entity.TestResult;
import eskimo.invoker.enums.CompilationVerdict;
import eskimo.invoker.enums.TestVerdict;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GenerateProblemAnswersJob extends JudgeJob {

    private static final Logger logger = LoggerFactory.getLogger(GenerateProblemAnswersJob.class);

    private final Problem problem;

    private final ProgrammingLanguageService programmingLanguageService;
    private final StorageService storageService;
    private final ProblemService problemService;
    private final InvokerService invokerService;

    private ProgrammingLanguage solutionLanguage;
    private File solutionFile;
    private CompilationResult compilationResult;
    private TestResult[] genResults;

    public GenerateProblemAnswersJob(Problem problem,
                                     ProgrammingLanguageService programmingLanguageService,
                                     StorageService storageService,
                                     ProblemService problemService,
                                     InvokerService invokerService) {
        this.problem = problem;
        this.programmingLanguageService = programmingLanguageService;
        this.storageService = storageService;
        this.problemService = problemService;
        this.invokerService = invokerService;
    }

    @Override
    public void execute() {
        try {
            if (!init()) {
                updateStatus(GenerationStatus.ERROR, "Can't match reference solutions and available compilers");
                return;
            }
            CompileJob compileJob = new CompileJob(invokerService, solutionLanguage, solutionFile);
            compileJob.execute(invoker);
            compilationResult = compileJob.getCompilationResult();
            if (compilationResult.getVerdict() != CompilationVerdict.SUCCESS) {
                String message = this.compilationResult.getVerdict().name() + "\n" +
                        this.compilationResult.getCompilerStdout() + "\n" +
                        this.compilationResult.getCompilerStderr();
                updateStatus(GenerationStatus.ERROR, message);
                return;
            }
            generate();
            if (!validate()) {
                return;
            }
            save();
            updateStatus(GenerationStatus.DONE, "Done");
        } catch (Throwable e) {
            updateStatus(GenerationStatus.ERROR, e.getMessage());
            logger.error("Error while generating problem answers", e);
        }
    }

    private boolean init() {
        for (File solution : storageService.getSolutionFiles(problem.getContestId(), problem.getIndex(), "main")) {
            String extension = FilenameUtils.getExtension(solution.getName());
            solutionLanguage = programmingLanguageService.getProgrammingLanguageByExtension(extension);
            if (solutionLanguage != null) {
                solutionFile = solution;
                return true;
            }
        }
        return false;
    }

    private void updateStatus(GenerationStatus status, String message) {
        problem.setAnswersGenerationStatus(status);
        problem.setAnswersGenerationMessage(message);
        problemService.updateAnswerGenerationProblemStatuses(problem);
    }

    private void generate() {
        TestLazyParams testParams = new TestLazyParams();
        testParams.setExecutable(compilationResult.getExecutable());
        testParams.setExecutableName(FilenameUtils.getBaseName(solutionFile.getName()) + "." + solutionLanguage.getBinaryExtension());
        testParams.setCheckerDisabled(true);
        testParams.setContestId(problem.getContestId());
        testParams.setProblemId(problem.getId());
        testParams.setNumberTests(problem.getTestsCount());
        testParams.setStopOnFirstFail(false);
        testParams.setRunCommand(solutionLanguage.getRunCommand());
        testParams.setInputName("input.txt");
        testParams.setOutputName("output.txt");
        testParams.setTimeLimit(problem.getTimeLimit() * 2);
        testParams.setMemoryLimit(problem.getMemoryLimit() * 2);
        genResults = invokerService.test(invoker, testParams);
    }

    private boolean validate() {
        if (genResults == null || genResults.length != problem.getTestsCount()) {
            updateStatus(GenerationStatus.ERROR, "Answer from invoker is incorrect");
            return false;
        }
        StringBuilder badResult = new StringBuilder();
        for (TestResult result : genResults) {
            if (result.getVerdict() != TestVerdict.CHECKER_DISABLED) {
                badResult.append(result.getVerdict().name())
                        .append(" ")
                        .append(result.getMessage())
                        .append(" on test ")
                        .append(result.getIndex())
                        .append(" (Used time: " + result.getUsedTime() + "ms, ")
                        .append("Used memory: " + result.getUsedMemory() + "Kb)\n");
            }
        }
        if (badResult.length() > 0) {
            updateStatus(GenerationStatus.ERROR, badResult.toString());
            return false;
        }
        return true;
    }

    private void save() {
        List<StorageOrder> storageOrders = new ArrayList<>();
        for (TestResult result : genResults) {
            storageOrders.add(new StorageOrderCreateFile(
                    storageService.getTestAnswerFile(problem.getContestId(), problem.getIndex(), result.getIndex()),
                    result.getOutputData()));
        }
        storageService.executeOrders(storageOrders);
    }
}
