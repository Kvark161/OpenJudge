package eskimo.backend.services;

import eskimo.backend.dao.ProblemDao;
import eskimo.backend.dao.StatementsDao;
import eskimo.backend.domain.Problem;
import eskimo.backend.domain.Statement;
import eskimo.backend.rest.response.ProblemInfoResponse;
import eskimo.backend.rest.response.StatementsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@Service
public class ProblemService {
    private static final Logger logger = LoggerFactory.getLogger(ProblemService.class);

    private final ProblemDao problemDao;
    private final StatementsDao statementsDao;

    @Autowired
    public ProblemService(ProblemDao problemDao, StatementsDao statementsDao) {
        this.problemDao = problemDao;
        this.statementsDao = statementsDao;
    }

    public List<ProblemInfoResponse> getContestProblems(Long contestId) {
        List<Problem> contestProblems = problemDao.getContestProblems(contestId);
        Map<Long, String> problemNames = statementsDao.getProblemNames(contestId);
        return contestProblems.stream()
                .map(problem -> {
                    ProblemInfoResponse response = new ProblemInfoResponse();
                    response.fillProblemFields(problem);
                    response.setName(problemNames.get(problem.getId()));
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
}
