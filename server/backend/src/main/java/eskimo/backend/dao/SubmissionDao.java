package eskimo.backend.dao;

import eskimo.backend.domain.Submission;
import eskimo.backend.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SubmissionDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public SubmissionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public List<Submission> getAllSubmissions() {
        String sql = "SELECT * FROM submissions ORDER BY sending_date_time DESC";
        return jdbcTemplate.query(sql, new SubmissionRowMapper());
    }

    @Transactional
    public Submission getSubmissionById(Long id) {
        String sql = "SELECT * FROM submissions WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new SubmissionRowMapper(), id);
    }

    @Transactional
    public List<Submission> getUserSubmissions(Long userId) {
        String sql = "SELECT * FROM submissions WHERE user_id = ? ORDER BY sending_date_time DESC";
        return jdbcTemplate.query(sql, new SubmissionRowMapper(), userId);
    }

    public List<Submission> getUserSubmissions(Long userId, Long contestId) {
        String sql = "SELECT * " +
                "FROM submissions " +
                "WHERE user_id = ? AND contest_id = ? " +
                "ORDER BY sending_date_time DESC";
        return jdbcTemplate.query(sql, new SubmissionRowMapper(), userId, contestId);
    }

    @Transactional
    public void insertSubmission(Submission submission) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("submissions")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", submission.getUser().getId());
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
                "user_id = ?, " +
                "contest_id = ?, " +
                "problem_id = ?, " +
                "source_code = ?, " +
                "status = ? ," +
                "sending_date_time = ? ," +
                "number_tests = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql,
                submission.getUser().getId(),
                submission.getContestId(),
                submission.getProblemId(),
                submission.getSourceCode(),
                submission.getStatus().name(),
                Timestamp.valueOf(submission.getSendingDateTime()),
                submission.getNumberTests(),
                submission.getId());
    }

    private static class SubmissionRowMapper implements RowMapper<Submission> {
        @Override
        public Submission mapRow(ResultSet resultSet, int i) throws SQLException {
            Submission submission = new Submission();
            submission.setId(resultSet.getLong("id"));
            User user = new User();
            user.setId(resultSet.getLong("user_id"));
            submission.setUser(user);
            submission.setContestId(resultSet.getLong("contest_id"));
            submission.setProblemId(resultSet.getLong("problem_id"));
            submission.setSourceCode(resultSet.getString("source_code"));
            submission.setStatus(Submission.Status.valueOf(resultSet.getString("status")));
            submission.setSendingDateTime(resultSet.getTimestamp("sending_date_time").toLocalDateTime());
            submission.setNumberTests(resultSet.getInt("number_tests"));
            return submission;
        }
    }
}
