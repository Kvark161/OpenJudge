package eskimo.backend.judge.jobs;

import eskimo.backend.entity.Problem;
import eskimo.backend.entity.ProgrammingLanguage;
import eskimo.backend.entity.enums.ProblemAnswersGenerationStatus;
import eskimo.backend.services.InvokerService;
import eskimo.backend.services.ProblemService;
import eskimo.backend.services.ProgrammingLanguageService;
import eskimo.backend.storage.StorageOrder;
import eskimo.backend.storage.StorageOrderCreateFile;
import eskimo.backend.storage.StorageService;
import eskimo.invoker.entity.CompilationParams;
import eskimo.invoker.entity.CompilationResult;
import eskimo.invoker.entity.TestLazyParams;
import eskimo.invoker.entity.TestResult;
import eskimo.invoker.enums.CompilationVerdict;
import eskimo.invoker.enums.TestVerdict;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GenerateProblemAnswersJob extends JudgeJob {

    private final Problem problem;

    private final ProgrammingLanguageService programmingLanguageService;
    private final StorageService storageService;
    private final ProblemService problemService;
    private final InvokerService invokerService;

    private ProgrammingLanguage solutionLanguage;
    private File solutionFile;
    private CompilationResult compilationResult;
    private CompilationParams compilationParams;
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
                updateStatus(ProblemAnswersGenerationStatus.ERROR, "Can't match reference solutions and available compilers");
                return;
            }
            if (!compile()) {
                String message = compilationResult.getVerdict().name() + "\n" +
                        compilationResult.getCompilerStdout() + "\n" +
                        compilationResult.getCompilerStderr();
                updateStatus(ProblemAnswersGenerationStatus.ERROR, message);
                return;
            }
            generate();
            save();
        } catch (Throwable e) {
            updateStatus(ProblemAnswersGenerationStatus.ERROR, e.getMessage());
        }
    }

    private boolean init() {
        List<ProgrammingLanguage> languages = programmingLanguageService.getAllProgrammingLanguages();
        for (File solution : storageService.getSolutionFiles(problem.getContestId(), problem.getIndex(), "main")) {
            for (ProgrammingLanguage language : languages) {
                String extension = FilenameUtils.getExtension(solution.getName());
                if (language.getExtension().equals(extension)) {
                    solutionLanguage = language;
                    solutionFile = solution;
                    return true;
                }
            }
        }
        return false;
    }

    private void updateStatus(ProblemAnswersGenerationStatus status, String message) {
        problem.setAnswersGenerationStatus(status);
        problem.setAnswersGenerationMessage(message);
        problemService.updateProblemStatuses(problem);
    }

    private boolean compile() {
        compilationParams = new CompilationParams();
        compilationParams.setMemoryLimit(solutionLanguage.getCompilationMemoryLimit());
        compilationParams.setTimeLimit(solutionLanguage.getCompilationTimeLimit());
        compilationParams.setSourceFileName(solutionFile.getName());
        compilationParams.setExecutableFileName(FilenameUtils.getBaseName(solutionFile.getName()) + '.' + solutionLanguage.getBinaryExtension());
        compilationParams.setCompilationCommand(solutionLanguage.getCompileCommand());
        compilationParams.setCompilerPath(solutionLanguage.getCompilerPath());
        compilationParams.setCompilerName(solutionLanguage.getName());
        compilationResult = invokerService.compile(invoker, compilationParams);
        return CompilationVerdict.SUCCESS == compilationResult.getVerdict();
    }

    private void generate() {
        TestLazyParams testParams = new TestLazyParams();
        testParams.setExecutable(compilationResult.getExecutable());
        testParams.setExecutableName(compilationParams.getExecutableFileName());
        testParams.setCheckerDisabled(true);
        testParams.setContestId(problem.getContestId());
        testParams.setProblemId(problem.getId());
        testParams.setNumberTests(problem.getTestsCount());
        testParams.setStopOnFirstFail(false);
        genResults = invokerService.test(invoker, testParams);
    }

    private boolean validate() {
        if (genResults == null || genResults.length != problem.getTestsCount()) {
            updateStatus(ProblemAnswersGenerationStatus.ERROR, "Answer from invoker is incorrect");
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
            updateStatus(ProblemAnswersGenerationStatus.ERROR, badResult.toString());
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
