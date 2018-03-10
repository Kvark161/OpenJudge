package eskimo.backend.dao;

import eskimo.backend.domain.Problem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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

    @Autowired
    public ProblemDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
        return jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(params)).longValue();
    }

    @Transactional
    public List<Problem> getContestProblems(Long contestId) {
        String sql = "SELECT id, contest_id, contest_index, time_limit, memory_limit, contest_id FROM problems " +
                " WHERE contest_id = ?" +
                " ORDER BY contest_index";
        return jdbcTemplate.query(sql, new Object[]{contestId}, ROW_MAPPER);
    }

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
    public Problem getProblem(Long id) {
        String sql = "SELECT id, contest_id, contest_index, time_limit, memory_limit FROM problems " +
                " WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, ROW_MAPPER);
    }

    public Problem getContestProblem(Long contestId, Integer problemIndex) {
        String sql = "SELECT id, contest_id, contest_index, time_limit, memory_limit FROM problems " +
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

    private static class ProblemRowMapper implements RowMapper<Problem> {
        @Override
        public Problem mapRow(ResultSet resultSet, int i) throws SQLException {
            Problem problem = new Problem();
            problem.setId(resultSet.getLong("id"));
            problem.setIndex(resultSet.getLong("contest_index"));
            problem.setTimeLimit(resultSet.getLong("time_limit"));
            problem.setMemoryLimit(resultSet.getLong("memory_limit"));
            problem.setContestId(resultSet.getLong("contest_id"));
            return problem;
        }
    }

}
