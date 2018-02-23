package eskimo.backend.storage;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;

public class TemporaryFile implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(TemporaryFile.class);

    @Getter
    private final File file;

    public TemporaryFile(File file) {
        this.file = file;
    }

    @Override
    public void close() {
        try {
            org.apache.commons.io.FileUtils.forceDelete(file);
        } catch (Exception e) {
            logger.error("Can't delete temp file: " + file, e);
        }
    }
}
