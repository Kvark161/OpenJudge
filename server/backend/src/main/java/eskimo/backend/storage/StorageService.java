package eskimo.backend.storage;

import eskimo.backend.config.AppSettingsProvider;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class StorageService {

    private static final Logger logger = LoggerFactory.getLogger(StorageService.class);

    private static final String CONTEST_ID_FORMAT = "000000";
    private static final String CONTEST_FOLDER_NAME = "contests";
    private static final String STATEMENTS_FOLDER_NAME = "statements";
    private static final String PROBLEMS_FOLDER_NAME = "problems";
    private static final String CHECKERS_FILE_NAME = "checker.cpp";
    private static final String CHECKERS_EXE_NAME = "checker.exe";
    private static final String VALIDATORS_FILE_NAME = "validator.cpp";
    private static final String SOLUTIONS_FOLDER_NAME = "solutions";
    private static final String TESTS_FOLDER_NAME = "tests";
    private static final String STATEMENTS_ENGLISH = "english";
    private static final String STATEMENTS_RUSSIAN = "russian";

    private AppSettingsProvider appSettingsProvider;

    @Autowired
    public StorageService(AppSettingsProvider appSettingsProvider) {
        this.appSettingsProvider = appSettingsProvider;
    }

    public File getContestFolder(long contestId) {
        return new File(appSettingsProvider.getStoragePath() + File.separator + CONTEST_FOLDER_NAME + File.separator +
                new DecimalFormat(CONTEST_ID_FORMAT).format(contestId));
    }

    private File getStatementsFolder(long contestId, long problemIndex) {
        return new File(getProblemFolder(contestId, problemIndex) + File.separator + STATEMENTS_FOLDER_NAME);
    }

    /**
     * Returns path to existing json file with statements for appropriate contest and problem.
     * If file for given language doesn't exist, returns another one (only english and russian supported).
     * If both files don't exist or input language is bad, returns null.
     */
    public File getStatementFile(long contestId, long problemIndex, String language) {
        String storageLanguage = language.toLowerCase();
        if (!storageLanguage.equals(STATEMENTS_ENGLISH) && !storageLanguage.equals(STATEMENTS_RUSSIAN)) {
            logger.error("wrong language: {}", storageLanguage);
            return null;
        }
        String statementFileTemplate = getStatementsFolder(contestId, problemIndex) + File.separator + "%s" + ".json";
        File result = new File(String.format(statementFileTemplate, storageLanguage));
        if (!result.exists()) {
            String secondLanguage = storageLanguage.equals(STATEMENTS_ENGLISH) ? STATEMENTS_RUSSIAN : STATEMENTS_ENGLISH;
            result = new File(String.format(statementFileTemplate, secondLanguage));
        }
        if (!result.exists()) {
            logger.error("statements doesn't exists: {}.json", result.getName());
            return null;
        }
        return result;
    }

    private File getProblemFolder(long contestId, long problemIndex) {
        return new File(getContestFolder(contestId) + File.separator + PROBLEMS_FOLDER_NAME
                + File.separator + problemIndex);
    }

    public File getCheckerSourceFile(long contestId, long problemIndex) {
        return new File(getProblemFolder(contestId, problemIndex) + File.separator + CHECKERS_FILE_NAME);
    }

    public File getCheckerExe(long contestId, long problemIndex) {
        return new File(getProblemFolder(contestId, problemIndex) + File.separator + CHECKERS_EXE_NAME);
    }

    private File getTestsFolder(long contestId, long problemIndex) {
        return new File(getProblemFolder(contestId, problemIndex) + File.separator + TESTS_FOLDER_NAME);
    }

    public File getTestInputFile(long contestId, long problemIndex, long testIndex) {
        return new File(getTestsFolder(contestId, problemIndex) + File.separator + String.format("%03d", testIndex) + ".in");
    }

    public String getTestInputData(long contestId, long problemIndex, long testIndex) throws IOException {
        return FileUtils.readFileToString(getTestInputFile(contestId, problemIndex, testIndex));
    }

    public File getTestAnswerFile(long contestId, long problemIndex, long testIndex) {
        return new File(getTestsFolder(contestId, problemIndex) + File.separator + String.format("%03d", testIndex) + ".ans");
    }

    public String getTestAnswerData(long contestId, long problemIndex, long testIndex) throws IOException {
        return FileUtils.readFileToString(getTestAnswerFile(contestId, problemIndex, testIndex));
    }

    public File getValidatorFile(long contestId, long problemIndex) {
        return new File(getProblemFolder(contestId, problemIndex) + File.separator + VALIDATORS_FILE_NAME);
    }

    public File getSolutionFile(long contestId, long problemIndex, String name, String tag) {
        return new File(getSolutionsFolder(contestId, problemIndex, tag) + File.separator + name);
    }

    public List<File> getSolutionFiles(long contestId, long problemIndex, String tag) {
        File folder = getSolutionsFolder(contestId, problemIndex, tag);
        File[] solutions = folder.listFiles();
        if (solutions == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(solutions);
    }

    private File getSolutionsFolder(long contestId, long problemIndex, String tag) {
        String path = getProblemFolder(contestId, problemIndex) + File.separator + SOLUTIONS_FOLDER_NAME;
        if (tag != null && !"".equals(tag)) {
            path += File.separator + tag;
        }
        return new File(path);
    }

    public File getTestlib(long contestId, long problemIndex) {
        return new File(getProblemFolder(contestId, problemIndex) + File.separator + "testlib.h");
    }

    public void executeOrders(List<StorageOrder> orders) {
        int complete = 0;
        try {
            for (StorageOrder order : orders) {
                order.execute();
                ++complete;
            }
        } catch (Throwable e) {
            for (int i = complete - 1; i >= 0; --i) {
                try {
                    orders.get(i).rollback();
                } catch (Throwable ein) {
                    logger.error(ein.getMessage(), ein);
                }
            }
            throw new StorageOrderException(e);
        }
    }

}
