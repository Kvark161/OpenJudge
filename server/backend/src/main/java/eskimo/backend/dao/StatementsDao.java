package eskimo.backend.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eskimo.backend.domain.SampleTest;
import eskimo.backend.domain.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

@Repository
public class StatementsDao {
    private static final Logger logger = LoggerFactory.getLogger(StatementsDao.class);

    private static final StatementRowMapper ROW_MAPPER = new StatementRowMapper();
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public StatementsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long addStatements(Statement statement) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("statements")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> params = new HashMap<>();
        params.put("problem_id", statement.getProblemId());
        params.put("language", statement.getLanguage());
        params.put("input_file", statement.getInputFile());
        params.put("output_file", statement.getOutputFile());
        params.put("name", statement.getName());
        params.put("legend", statement.getLegend());
        params.put("input", statement.getInput());
        params.put("output", statement.getOutput());
        params.put("samples", getStringSampleTests(statement.getSampleTests()));
        params.put("notes", statement.getNotes());
        return jdbcInsert.executeAndReturnKey(params).longValue();
    }

    private String getStringSampleTests(List<SampleTest> sampleTests) {
        try {
            return JSON_MAPPER.writer().writeValueAsString(sampleTests);
        } catch (JsonProcessingException e) {
            logger.error("cannot convert sample tests to json");
            return null;
        }
    }

    public Statement getStatements(Long problemId, String language) {
        String sql = "SELECT id, problem_id, language, input_file, output_file, name, legend, input, output, " +
                "samples, notes FROM statements WHERE problem_id = ? AND language = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{problemId, language}, ROW_MAPPER);
    }

    public List<String> getSupportedLanguages(Long problemId) {
        String sql = "SELECT language FROM statements WHERE problem_id = ?";
        return jdbcTemplate.queryForList(sql, new Object[]{problemId}, String.class);
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

    private static class StatementRowMapper implements RowMapper<Statement> {
        @Override
        public Statement mapRow(ResultSet resultSet, int i) throws SQLException {
            Statement statement = new Statement();
            statement.setId(resultSet.getLong("id"));
            statement.setProblemId(resultSet.getLong("problem_id"));
            statement.setLanguage(resultSet.getString("language"));
            statement.setInputFile(resultSet.getString("input_file"));
            statement.setOutputFile(resultSet.getString("output_file"));
            statement.setName(resultSet.getString("name"));
            statement.setLegend(resultSet.getString("legend"));
            statement.setInput(resultSet.getString("input"));
            statement.setOutput(resultSet.getString("output"));
            String samples = resultSet.getString("samples");
            try {
                statement.setSampleTests(asList(JSON_MAPPER.readValue(samples, SampleTest[].class)));
            } catch (IOException e) {
                logger.error("can not parse sample tests from database");
            }
            statement.setNotes(resultSet.getString("notes"));
            return statement;
        }
    }
}
