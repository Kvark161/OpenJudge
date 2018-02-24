package eskimo.backend.dao;

import eskimo.backend.domain.Statement;
import eskimo.backend.storage.StorageService;
import eskimo.backend.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StatementsDao {

    private static final Logger logger = LoggerFactory.getLogger(StatementsDao.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private StorageService storageService;

    public void insertStatement(List<Statement> statements, Long contestId) {
        String sql = "INSERT INTO statements (contest_id, language, file_name) "
                + "VALUES (?,?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {

                Statement statement = statements.get(i);
                ps.setLong(1, contestId);
                ps.setString(2, statement.getLanguage());
                ps.setString(3, statement.getFileName());
            }

            @Override
            public int getBatchSize() {
                return statements.size();
            }
        });
    }

    public List<Statement> getAllStatements(Long contestId) {
        try {
            String sql = "SELECT id, language, file_name FROM statements WHERE contest_id = ?";
            return jdbcTemplate.query(sql, new Object[]{contestId}, new StatementsRowMapper());
        } catch (EmptyResultDataAccessException e) {
            logger.warn("can not get statements by contest id = " + contestId, e);
            return null;
        }
    }

    public Statement getStatement(Long contestId, String language) {
        try {
            String sql = "SELECT id, language, file_name FROM statements WHERE contest_id = ? AND language = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{contestId, language}, new StatementsRowMapper());
        } catch (EmptyResultDataAccessException e) {
            logger.warn("can not get statements: contestId = " + contestId + ", language = " + language, e);
            return null;
        }
    }

    private static class StatementsRowMapper implements RowMapper<Statement> {
        @Override
        public Statement mapRow(ResultSet resultSet, int i) throws SQLException {
            Statement statement = new Statement();
            statement.setId(resultSet.getLong("id"));
            statement.setLanguage(resultSet.getString("language"));
            statement.setFileName(resultSet.getString("file_path"));
            return statement;
        }
    }
}
