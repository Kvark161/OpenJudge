package eskimo.backend;

import eskimo.backend.config.AppSettings;
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
    private AppSettings appSettings;

    @Before
    public void clean() throws IOException {
        FileUtils.deleteDirectory(appSettings.getStoragePath());
        FileUtils.deleteDirectory(appSettings.getTempPath());
        flyway.clean();
        flyway.migrate();
    }

}
