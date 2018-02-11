package eskimo.backend.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.File;

/**
 * Created by Sokirkina Ekaterina on 02-Feb-2017.
 */
@Slf4j
public class TemporaryFile implements Closeable {

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
            log.error("can't delete temp file = " + file, e);
        }
    }
}
