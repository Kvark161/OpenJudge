package eskimo.backend.storage;

import eskimo.backend.config.AppSettings;
import eskimo.backend.domain.Statement;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

@Service
public class StorageService {

    private static final Logger logger = LoggerFactory.getLogger(StorageService.class);

    private static final String CONTEST_ID_FORMAT = "000000";
    private static final String CONTEST_FOLDER_NAME = "contests";
    private static final String STATEMENTS_FOLDER_NAME = "statements";
    private static final String PROBLEMS_FOLDER_NAME = "problems";
    private static final String CHECKERS_FILE_NAME = "checker.cpp";
    private static final String VALIDATORS_FILE_NAME = "validator.cpp";
    private static final String SOLUTIONS_FOLDER_NAME = "solutions";
    private static final String TESTS_FOLDER_NAME = "tests";

    @Autowired
    private AppSettings appSettings;

    public File getContestFolder(long contestId) {
        return new File(appSettings.getStoragePath() + File.separator + CONTEST_FOLDER_NAME + File.separator +
                new DecimalFormat(CONTEST_ID_FORMAT).format(contestId));
    }

    public File getStatementsFolder(long contestId) {
        return new File(getContestFolder(contestId) + File.separator + STATEMENTS_FOLDER_NAME);
    }

    public File getStatementsFolder(long contestId, long problemIndex) {
        return new File(getProblemFolder(contestId, problemIndex) + File.separator + STATEMENTS_FOLDER_NAME);
    }

    public File getStatementFile(long contestId, String language, String format) {
        return new File(getStatementsFolder(contestId) + File.separator + language + "." + format);
    }

    public File getStatementFile(long contestId, long problemIndex, String language, String format) {
        return new File(getStatementsFolder(contestId, problemIndex) + File.separator + language.toLowerCase() + "." + format.toLowerCase());
    }

    public File getStatementFile(long contestId) {
        return getStatementFile(contestId, Statement.DEFAULT_LANGUAGE, Statement.DEFAULT_FORMAT);
    }

    public File getProblemFolder(long contestId, long problemIndex) {
        return new File(getContestFolder(contestId) + File.separator + PROBLEMS_FOLDER_NAME
                + File.separator + problemIndex);
    }

    public File getCheckerFile(long contestId, long problemIndex) {
        return new File(getProblemFolder(contestId, problemIndex) + File.separator + CHECKERS_FILE_NAME);
    }

    public File getTestsFolder(long contestId, long problemIndex) {
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
        String path = getProblemFolder(contestId, problemIndex) + File.separator + SOLUTIONS_FOLDER_NAME;
        if (tag != null && !"".equals(tag)) {
            path += File.separator + tag;
        }
        return new File(path + File.separator + name);
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
