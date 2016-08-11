package com.klevleev.eskimo.server.web.utils;

import com.klevleev.eskimo.server.core.ApplicationSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Stepan Klevleev on 11-Aug-16.
 */
@Component
public class FileUtils {

	private final ApplicationSettings settings;

	@Autowired
	public FileUtils(ApplicationSettings applicationSettings) {
		this.settings = applicationSettings;
	}

	public File saveFile(MultipartFile file) throws IOException {
		byte[] bytes = file.getBytes();
		File dir = new File(settings.getTempPath() + File.separator + "uploaded");
		if (!dir.exists()) {
			//noinspection ResultOfMethodCallIgnored
			dir.mkdirs();
		}

		File filePath = File.createTempFile("contest-", ".zip", dir);
		try (FileOutputStream fos = new FileOutputStream(filePath);
		     BufferedOutputStream stream = new BufferedOutputStream(fos)) {
			stream.write(bytes);
		}
		return filePath;
	}

	public File unzip(File zipFile) throws IOException {
		byte[] buffer = new byte[1024];
		Path outputFolder = Files.createTempDirectory(new File(settings.getTempPath()).toPath(), "contest-");
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
}
