package com.klevleev.eskimo.server.core.utils;

import com.klevleev.eskimo.server.core.config.ApplicationSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Stepan Klevleev on 11-Aug-16.
 */
@Component
public class FileUtils {

	private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

	private final ApplicationSettings settings;

	@Autowired
	public FileUtils(ApplicationSettings applicationSettings) {
		this.settings = applicationSettings;
	}

	public File unzip(File zipFile) throws IOException {
		byte[] buffer = new byte[1024];
		Path outputFolder = Files.createTempDirectory(settings.getTempRoot().toPath(), "contest-");
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

	public File copyFileToFolder(File file, File folder) throws IOException{
		//noinspection ResultOfMethodCallIgnored
		folder.mkdirs();
		File result = new File(folder + File.separator + file.getName());
		Files.copy(file.toPath(), result.toPath());
		return result;
	}

}
