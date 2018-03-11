package eskimo.backend.storage;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class StorageOrderCreateFile extends StorageOrder {

    private String data;
    private File file;

    public StorageOrderCreateFile(File file, String data) {
        this.file = file;
        this.data = data;
    }

    @Override
    void execute() throws StorageOrderException {
        try {
            file.getParentFile().mkdirs();
            FileUtils.write(file, data);
        } catch (IOException e) {
            throw new StorageOrderException("Can't create file " + file.getAbsolutePath(), e);
        }
    }

    @Override
    void rollback() {
        if (file.exists()) {
            file.delete();
        }
    }
}
