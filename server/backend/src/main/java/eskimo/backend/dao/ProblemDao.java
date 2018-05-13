package eskimo.backend.dao;

import eskimo.backend.entity.Problem;
import eskimo.backend.entity.enums.GenerationStatus;
import eskimo.backend.rest.request.EditProblemRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ProblemDao {

    private static final ProblemRowMapper ROW_MAPPER = new ProblemRowMapper();

    private JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public ProblemDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Long insertProblem(Problem problem) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("problems")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> params = new HashMap<>();
        params.put("contest_id", problem.getContestId());
        params.put("time_limit", problem.getTimeLimit());
        params.put("memory_limit", problem.getMemoryLimit());
        params.put("tests_count", problem.getTestsCount());
        params.put("contest_index", problem.getIndex());
        params.put("answers_generation_status", problem.getAnswersGenerationStatus().name());
        params.put("answers_generation_message", problem.getAnswersGenerationMessage());
        return jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(params)).longValue();
    }

    @Transactional
    public List<Problem> getContestProblems(Long contestId) {
        String sql = "SELECT * FROM problems " +
                " WHERE contest_id = ?" +
                " ORDER BY contest_index";
        return jdbcTemplate.query(sql, new Object[]{contestId}, ROW_MAPPER);
    }

    @Transactional
    public Map<Long, String> getProblemNames(Long contestId) {
        String sql = "SELECT s.problem_id, s.name FROM contests as c " +
                "JOIN problems as p on c.id = p.contest_id " +
                "JOIN statements as s on p.id = s.problem_id " +
                "WHERE c.id = ?";
        Map<Long, String> problemNameById = new HashMap<>();
        jdbcTemplate.query(sql, new Object[]{contestId}, row -> {
            long problemId = row.getLong("problem_id");
            String problemName = row.getString("name");
            problemNameById.put(problemId, problemName);
        });
        return problemNameById;
    }

    @Transactional
    public Map<Long, String> getProblemNames(List<Long> problemIds) {
        String sql = "SELECT s.problem_id, s.name FROM contests as c " +
                "JOIN problems as p on c.id = p.contest_id " +
                "JOIN statements as s on p.id = s.problem_id " +
                "WHERE c.id in (:ids)";
        Map<Long, String> problemNameById = new HashMap<>();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", problemIds);
        namedParameterJdbcTemplate.query(sql, parameters, row -> {
            long problemId = row.getLong("problem_id");
            String problemName = row.getString("name");
            problemNameById.put(problemId, problemName);
        });
        return problemNameById;
    }

    @Transactional
    public Problem getProblem(Long id) {
        String sql = "SELECT * FROM problems " +
                " WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, ROW_MAPPER);
    }

    @Transactional
    public Problem getContestProblem(Long contestId, Long problemIndex) {
        String sql = "SELECT * FROM problems " +
                " WHERE contest_id = ? AND contest_index = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{contestId, problemIndex}, ROW_MAPPER);
    }

    @Transactional
    public int getNextProblemIndex(Long contestId) {
        String sql = "SELECT MAX(contest_index) FROM problems " +
                " WHERE contest_id = ?";
        Integer value = jdbcTemplate.queryForObject(sql, new Object[]{contestId}, Integer.class);
        if (value == null) {
            return 1;
        }
        return value + 1;
    }

    public void editContestProblem(Long contestId, Long problemIndex, EditProblemRequest editProblemRequest) {
        String sql = "UPDATE problems " +
                "SET " +
                "time_limit = ?," +
                "memory_limit = ? " +
                "WHERE contest_id = ? AND contest_index = ?";
        jdbcTemplate.update(sql,
                editProblemRequest.getTimeLimit(),
                editProblemRequest.getMemoryLimit(),
                contestId, problemIndex);
    }

    @Transactional
    public void updateAnswerGenerationProblemStatuses(Problem problem) {
        String sql = "UPDATE problems " +
                "SET " +
                "answers_generation_status = ?," +
                "answers_generation_message = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql,
                problem.getAnswersGenerationStatus().name(),
                problem.getAnswersGenerationMessage(),
                problem.getId());
    }

    @Transactional
    public void updateCheckerCompilationProblemStatuses(long id, GenerationStatus status, String message) {
        String sql = "UPDATE problems " +
                "SET " +
                "checker_compilation_status = ?," +
                "checker_compilation_message = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql, status.name(), message, id);
    }

    @Transactional
    public void changeHiddenness(Long contestId, Long problemIndex, boolean hidden) {
        String sql = "UPDATE problems " +
                "SET " +
                "hidden = ? " +
                "WHERE contest_id = ? AND contest_index = ?";
        jdbcTemplate.update(sql, hidden, contestId, problemIndex);
    }

    private static class ProblemRowMapper implements RowMapper<Problem> {
        @Override
        public Problem mapRow(ResultSet rs, int i) throws SQLException {
            Problem problem = new Problem();
            problem.setId(rs.getLong("id"));
            problem.setIndex(rs.getLong("contest_index"));
            problem.setTimeLimit(rs.getLong("time_limit"));
            problem.setMemoryLimit(rs.getLong("memory_limit"));
            problem.setContestId(rs.getLong("contest_id"));
            problem.setHidden(rs.getBoolean("hidden"));
            problem.setAnswersGenerationStatus(GenerationStatus.valueOf(rs.getString("answers_generation_status")));
            problem.setAnswersGenerationMessage(rs.getString("answers_generation_message"));
            problem.setTestsCount(rs.getInt("tests_count"));
            return problem;
        }
    }

}
