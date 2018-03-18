package eskimo.backend;

import eskimo.backend.config.AppSettingsProvider;
import org.apache.commons.io.FileUtils;
import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@Ignore
public class BaseTest {

    @Autowired
    private Flyway flyway;

    @Autowired
    private AppSettingsProvider appSettingsProvider;

    @Before
    public void clean() throws IOException {
        FileUtils.deleteDirectory(appSettingsProvider.getStoragePath());
        FileUtils.deleteDirectory(appSettingsProvider.getTempPath());
        flyway.clean();
        flyway.migrate();
    }

}
