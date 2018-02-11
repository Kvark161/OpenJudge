package eskimo.backend.storage;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Stepan Klevleev on 30-Apr-17.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class StorageOrderCopyFile extends StorageOrder {

    private File sourceFile;
    private File targetFile;

    public StorageOrderCopyFile(File sourceFile, File targetFile) {
        this.sourceFile = sourceFile;
        this.targetFile = targetFile;
    }

    @Override
    void execute() throws StorageOrderException {
        try {
            targetFile.getParentFile().mkdirs();
            FileUtils.copyFile(sourceFile, targetFile);
        } catch (IOException e) {
            throw new StorageOrderException("can't copy file " + sourceFile.getPath() + " to file " + targetFile.getPath(), e);
        }
    }

    @Override
    void rollback() {
        if (targetFile.exists()) {
            targetFile.delete();
        }
    }
}
