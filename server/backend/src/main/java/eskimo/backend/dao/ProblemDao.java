package eskimo.backend.dao;

import eskimo.backend.domain.Problem;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ProblemDao {

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
        params.put("name", problem.getName());
        params.put("time_limit", problem.getTimeLimit());
        params.put("memory_limit", problem.getMemoryLimit());
        params.put("tests_count", problem.getTestsCount());
        params.put("contest_index", problem.getIndex());
        return jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(params)).longValue();
    }

    @Transactional
    public List<Problem> getContestProblems(Long contestId) {
        String sql = "SELECT id, contest_index, name, time_limit, memory_limit, contest_id FROM problems " +
                " WHERE contest_id = ?" +
                " ORDER BY contest_index";
        return jdbcTemplate.query(sql, new Object[]{contestId}, new ProblemRowMapper());
    }

    @Transactional
    public Problem getProblem(Long id) {
        String sql = "SELECT id, contest_index, name, time_limit, memory_limit FROM problems " +
                " WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, new ProblemDao.ProblemRowMapper());
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
            problem.setName(resultSet.getString("name"));
            problem.setTimeLimit(resultSet.getLong("time_limit"));
            problem.setMemoryLimit(resultSet.getLong("memory_limit"));
            problem.setContestId(resultSet.getLong("contest_id"));
            return problem;
        }
    }

}
