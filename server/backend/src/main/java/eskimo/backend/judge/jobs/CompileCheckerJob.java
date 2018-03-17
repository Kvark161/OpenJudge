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
import eskimo.invoker.enums.CompilationVerdict;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.function.Consumer;

public class CompileCheckerJob extends CompileJob {

    private static final Logger logger = LoggerFactory.getLogger(CompileCheckerJob.class);

    public CompileCheckerJob(InvokerService invokerService, StorageService storageService,
                             ProgrammingLanguageService programmingLanguageService, ProblemService problemService,
                             Problem problem) {
        super(invokerService, null, null);
        File testlib = storageService.getTestlib(problem.getContestId(), problem.getIndex());
        if (!testlib.exists()) {
            logger.error("Cannot compile checker because testlib doesn't exist");
            return;
        }
        setTestLib(testlib);
        File checkerFile = storageService.getCheckerSourceFile(problem.getContestId(), problem.getIndex());
        setSourceFile(checkerFile);
        String extension = FilenameUtils.getExtension(checkerFile.getName());
        ProgrammingLanguage language = programmingLanguageService.getProgrammingLanguageByExtension(extension);
        setProgrammingLanguage(language);
        setOnStatusUpdateCallback(getCheckerCompilationStatusUpdateCallback(storageService, problemService, problem));
    }

    private Consumer<CompilationResult> getCheckerCompilationStatusUpdateCallback(StorageService storageService,
          ProblemService problemService, Problem problem) {
        return compilationResult -> {
            if (compilationResult == null) {
                problemService.updateCheckerCompilationStatuses(problem.getId(),
                        GenerationStatus.ERROR, "Internal error");
            } else {
                CompilationVerdict verdict = compilationResult.getVerdict();
                String message;
                GenerationStatus generationStatus;
                if (verdict == CompilationVerdict.SUCCESS) {
                    generationStatus = GenerationStatus.DONE;
                    message = "Done";
                    File checkerExe = storageService.getCheckerExe(problem.getContestId(), problem.getIndex());
                    StorageOrder order = new StorageOrderCreateFile(checkerExe, compilationResult.getExecutable());
                    storageService.executeOrders(Collections.singletonList(order));
                } else {
                    generationStatus = GenerationStatus.ERROR;
                    message = verdict.name() + "\n" +
                            compilationResult.getCompilerStdout() + "\n" +
                            compilationResult.getCompilerStderr();
                }

                problemService.updateCheckerCompilationStatuses(problem.getId(),
                        generationStatus, message);
            }
        };
    }
}
