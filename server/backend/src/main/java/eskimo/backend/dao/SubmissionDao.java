package eskimo.backend.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eskimo.backend.entity.Submission;
import eskimo.invoker.entity.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Repository
public class SubmissionDao {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    private final SubmissionRowMapper fullMapper;
    private final SubmissionRowMapper simpleMapper;

    @Autowired
    public SubmissionDao(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        fullMapper = new SubmissionRowMapper(objectMapper, true);
        simpleMapper = new SubmissionRowMapper(objectMapper, false);
    }

    @Transactional
    public List<Submission> getAllSubmissions() {
        String sql = "SELECT * FROM submissions as s JOIN users as u ON s.user_id = u.id" +
                " ORDER BY sending_date_time DESC";
        return jdbcTemplate.query(sql, simpleMapper);
    }

    @Transactional
    public Submission getSubmissionById(Long id) {
        String sql = "SELECT * FROM submissions as s JOIN users as u ON s.user_id = u.id WHERE s.id = ?";
        return jdbcTemplate.queryForObject(sql, simpleMapper, id);
    }

    @Transactional
    public List<Submission> getUserSubmissions(Long userId) {
        String sql = "SELECT * FROM submissions as s JOIN users as u ON s.user_id = u.id" +
                " WHERE user_id = ?" +
                " ORDER BY sending_date_time DESC";
        return jdbcTemplate.query(sql, simpleMapper, userId);
    }

    public List<Submission> getUserSubmissions(Long userId, Long contestId) {
        String sql = "SELECT * " +
                "FROM submissions " +
                "WHERE user_id = ? AND contest_id = ? " +
                "ORDER BY sending_date_time DESC";
        return jdbcTemplate.query(sql, simpleMapper, userId, contestId);
    }

    @Transactional
    public void insertSubmission(Submission submission) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("submissions")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", submission.getUserId());
        params.put("contest_id", submission.getContestId());
        params.put("problem_id", submission.getProblemId());
        params.put("source_code", submission.getSourceCode());
        params.put("status", submission.getStatus().toString());
        params.put("sending_date_time", Timestamp.valueOf(submission.getSendingDateTime()));
        params.put("number_tests", submission.getNumberTests());
        params.put("passed_tests", submission.getPassedTests());
        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(params));
        submission.setId(key.longValue());
    }

    @Transactional
    public void updateSubmission(Submission submission) {
        String sql = "UPDATE submissions " +
                "SET " +
                "status = ? ," +
                "used_time = ? ," +
                "used_memory = ? ," +
                "number_tests = ?, " +
                "passed_tests = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(sql,
                submission.getStatus().name(),
                submission.getUsedTime(),
                submission.getUsedMemory(),
                submission.getNumberTests(),
                submission.getPassedTests(),
                submission.getId());
    }

    public void updateSubmissionResultData(Submission submission) {
        String sql = "UPDATE submissions " +
                "SET " +
                "result_data = ?" +
                "WHERE id = ?";
        Arrays.sort(submission.getTestResults(), Comparator.comparingInt(TestResult::getIndex));
        try {
            jdbcTemplate.update(sql,
                    objectMapper.writeValueAsString(submission.getTestResults()),
                    submission.getId());
        } catch (JsonProcessingException e) {
            logger.warn("Can't write json of submission test results (submissionId = " + submission.getId() + ")", e);
        }
    }

    public Submission getFullSubmission(Long submissionId) {
        String sql = "SELECT submissions.*, users.name AS username FROM submissions JOIN users ON submissions.user_id = users.id WHERE submissions.id = ?";
        return jdbcTemplate.queryForObject(sql, fullMapper, submissionId);
    }

    private static class SubmissionRowMapper implements RowMapper<Submission> {

        private final boolean isFull;
        private final ObjectMapper objectMapper;
        private static final Logger logger = LoggerFactory.getLogger(SubmissionDao.class);

        SubmissionRowMapper(ObjectMapper objectMapper, boolean isFull) {
            this.objectMapper = objectMapper;
            this.isFull = isFull;
        }

        @Override
        public Submission mapRow(ResultSet resultSet, int i) throws SQLException {
            Submission submission = new Submission();
            submission.setId(resultSet.getLong("id"));
            submission.setUserId(resultSet.getLong("user_id"));
            submission.setUsername(resultSet.getString("name"));
            submission.setContestId(resultSet.getLong("contest_id"));
            submission.setProblemId(resultSet.getLong("problem_id"));
            submission.setSourceCode(resultSet.getString("source_code"));
            submission.setStatus(Submission.Status.valueOf(resultSet.getString("status")));
            submission.setSendingDateTime(resultSet.getTimestamp("sending_date_time").toLocalDateTime());
            submission.setNumberTests(resultSet.getInt("number_tests"));
            submission.setPassedTests(resultSet.getInt("passed_tests"));
            if (isFull) {
                String resultData = resultSet.getString("result_data");
                try {
                    if (resultData != null && !"".equals(resultData)) {
                        TestResult[] testResults = objectMapper.readValue(resultData, TestResult[].class);
                        submission.setTestResults(testResults);
                    } else {
                        submission.setTestResults(new TestResult[0]);
                    }
                } catch (IOException e) {
                    logger.warn("Can't parse json:\n" + resultData);
                }
            }
            return submission;
        }
    }
}
