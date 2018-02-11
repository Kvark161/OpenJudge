package eskimo.backend.storage;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Stepan Klevleev on 30-Apr-17.
 */
public class StorageOrderCreateFolder extends StorageOrder {

    private File folder;

    public StorageOrderCreateFolder(File folder) {
        this.folder = folder;
    }

    @Override
    void execute() throws StorageOrderException {
        //noinspection ResultOfMethodCallIgnored
        folder.mkdirs();
    }

    @Override
    void rollback() {
        try {
            FileUtils.deleteDirectory(folder);
        } catch (IOException e) {
            throw new RuntimeException("can't delete folder: " + folder.getPath());
        }
    }
}
