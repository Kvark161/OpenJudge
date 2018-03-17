package eskimo.backend.storage;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class StorageOrderCreateFile extends StorageOrder {

    private String data;
    private byte[] binData;
    private File file;

    public StorageOrderCreateFile(File file, String data) {
        this.file = file;
        this.data = data;
    }

    public StorageOrderCreateFile(File file, byte[] data) {
        this.file = file;
        this.binData = data;
    }

    @Override
    void execute() throws StorageOrderException {
        try {
            file.getParentFile().mkdirs();
            if (binData != null) {
                FileUtils.writeByteArrayToFile(file, binData);
            } else {
                FileUtils.writeStringToFile(file, data);
            }
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
