package eskimo.backend.storage;

import eskimo.backend.domain.Statement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Stepan Klevleev on 30-Apr-17.
 */
@Service
@Slf4j
public class StorageService {

    private static final String CONTEST_ID_FORMAT = "000000";
    private static final String CONTEST_FOLDER_NAME = "contests";
    private static final String STATEMENTS_FOLDER_NAME = "statements";
    private static final String PROBLEMS_FOLDER_NAME = "problems";
    private static final String CHECKERS_FOLDER_NAME = "checkers";
    private static final String VALIDATORS_FOLDER_NAME = "validators";
    private static final String SOLUTIONS_FOLDER_NAME = "solutions";
    private static final String TESTS_FOLDER_NAME = "tests";

    @Value("${eskimo.storage.path}")
    private String root;

    public File getContestFolder(long contestId) {
        return new File(root + File.separator + CONTEST_FOLDER_NAME + File.separator +
                new DecimalFormat(CONTEST_ID_FORMAT).format(contestId));
    }

    public File getStatementsFolder(long contestId) {
        return new File(getContestFolder(contestId) + File.separator + STATEMENTS_FOLDER_NAME);
    }

    public File getStatementFile(long contestId, String language, String format) {
        return new File(getStatementsFolder(contestId) + File.separator + language + "." + format);
    }

    public File getStatementFile(long contestId) {
        return getStatementFile(contestId, Statement.DEFAULT_LANGUAGE, Statement.DEFAULT_FORMAT);
    }

    public File getProblemFolder(long contestId, long problemIndex) {
        return new File(getContestFolder(contestId) + File.separator + PROBLEMS_FOLDER_NAME
                + File.separator + problemIndex);
    }

    public File getCheckerFolder(long contestId, long problemIndex) {
        return new File(getProblemFolder(contestId, problemIndex) + File.separator + CHECKERS_FOLDER_NAME);
    }

    public File getTestsFolder(long contestId, long problemIndex) {
        return new File(getProblemFolder(contestId, problemIndex) + File.separator + TESTS_FOLDER_NAME);
    }

    public File getTestInputFile(long contestId, long problemIndex, long testIndex) {
        return new File(getTestsFolder(contestId, problemIndex) + File.separator + String.format("%03d", testIndex) + ".in");
    }

    public File getTestAnswerFile(long contestId, long problemIndex, long testIndex) {
        return new File(getTestsFolder(contestId, problemIndex) + File.separator + String.format("%03d", testIndex) + ".ans");
    }

    public File getValidatorFolder(long contestId, long problemIndex) {
        return new File(getProblemFolder(contestId, problemIndex) + File.separator + VALIDATORS_FOLDER_NAME);
    }

    public File getSolutionFolder(long contestId, long problemIndex) {
        return new File(getProblemFolder(contestId, problemIndex) + File.separator + SOLUTIONS_FOLDER_NAME);
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
                    log.error(ein.getMessage(), ein);
                }
            }
            throw new StorageOrderException(e);
        }
    }

}
