package eskimo.backend.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class StorageOrderDeleteProblem extends StorageOrder {
    private static final Logger logger = LoggerFactory.getLogger(StorageOrderDeleteProblem.class);

    private StorageService storageService;
    private Long contestId;
    private Long problemIndex;

    public StorageOrderDeleteProblem(StorageService storageService, Long contestId, Long problemIndex) {
        this.storageService = storageService;
        this.contestId = contestId;
        this.problemIndex = problemIndex;
    }

    @Override
    void execute() throws StorageOrderException {
        File problemFolder = storageService.getProblemFolder(contestId, problemIndex);
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(problemFolder);
        } catch (IOException e) {
            logger.error(String.format("Can't delete problem directory (contest %d, problem %d)",
                    contestId, problemIndex), e);
        }
    }

    @Override
    void rollback() {
        //no rollback actions
    }
}
