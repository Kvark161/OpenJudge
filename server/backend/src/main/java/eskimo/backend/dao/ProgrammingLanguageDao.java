package eskimo.backend.dao;

import eskimo.backend.entity.ProgrammingLanguage;
import org.apache.tools.ant.types.Commandline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ProgrammingLanguageDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ProgrammingLanguageDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ProgrammingLanguage> getAllProgrammingLanguages() {
        String sql = "SELECT * FROM programming_languages ORDER BY name";
        return jdbcTemplate.query(sql, new ProgrammingLanguageRowMapper());
    }

    public void insertProgrammingLanguage(ProgrammingLanguage programmingLanguage) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("programming_languages")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> params = new HashMap<>();
        params.put("name", programmingLanguage.getName());
        params.put("description", programmingLanguage.getDescription());
        jdbcInsert.execute(new MapSqlParameterSource(params));
    }

    public ProgrammingLanguage getProgrammingLanguage(Long id) {
        String sql = "SELECT * FROM programming_languages WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, new ProgrammingLanguageRowMapper());
    }

    private static class ProgrammingLanguageRowMapper implements RowMapper<ProgrammingLanguage> {

        @Override
        public ProgrammingLanguage mapRow(ResultSet resultSet, int i) throws SQLException {
            ProgrammingLanguage programmingLanguage = new ProgrammingLanguage();
            programmingLanguage.setId(resultSet.getLong("id"));
            programmingLanguage.setName(resultSet.getString("name"));
            programmingLanguage.setDescription(resultSet.getString("description"));
            programmingLanguage.setCompilerPath(resultSet.getString("compiler_path"));
            programmingLanguage.setCompiled(resultSet.getBoolean("is_compiled"));
            programmingLanguage.setInterpreterPath(resultSet.getString("interpreter_path"));
            programmingLanguage.setExtension(resultSet.getString("extension"));
            String[] compileCommand = Commandline.translateCommandline(resultSet.getString("compile_command"));
            programmingLanguage.setCompileCommand(Arrays.asList(compileCommand));
            String[] runCommand = Commandline.translateCommandline(resultSet.getString("run_command"));
            programmingLanguage.setRunCommand(Arrays.asList(runCommand));
            return programmingLanguage;
        }

    }
}
