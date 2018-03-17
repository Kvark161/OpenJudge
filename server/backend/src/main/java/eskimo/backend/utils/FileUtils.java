package eskimo.backend.utils;

import eskimo.backend.config.AppSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SuppressWarnings("ResultOfMethodCallIgnored")
@Component
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    @Autowired
    private AppSettings appSettings;

    public File unzip(File zipFile, String prefix) throws IOException {
        byte[] buffer = new byte[1024];
        appSettings.getTempPath().mkdirs();
        Path outputFolder = Files.createTempDirectory(Paths.get(appSettings.getTempPath().getAbsolutePath()), prefix);
        try (FileInputStream fis = new FileInputStream(zipFile);
             ZipInputStream zis = new ZipInputStream(fis)) {
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                if (!ze.isDirectory()) {
                    File newFile = new File(outputFolder + File.separator + fileName);
                    //noinspection ResultOfMethodCallIgnored
                    new File(newFile.getParent()).mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                ze = zis.getNextEntry();
            }
        }
        return outputFolder.toFile();
    }

    public File saveFile(MultipartFile file, String prefix, String suffix) throws IOException {
        File filePath = File.createTempFile(prefix, suffix, appSettings.getTempPath());
        byte[] bytes = file.getBytes();
        try (FileOutputStream fos = new FileOutputStream(filePath);
             BufferedOutputStream stream = new BufferedOutputStream(fos)) {
            stream.write(bytes);
        } catch (Throwable e) {
            logger.error("can't save file=" + file.getName(), e);
            deleteFile(filePath);
        }
        return filePath;
    }

    private void deleteFile(File file) {
        try {
            org.apache.commons.io.FileUtils.forceDelete(file);
        } catch (Throwable e) {
            logger.error("can't delete temp file=" + file, e);
        }
    }
}
