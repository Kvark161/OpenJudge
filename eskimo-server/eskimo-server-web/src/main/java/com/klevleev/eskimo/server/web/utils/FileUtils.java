package com.klevleev.eskimo.server.web.utils;

import com.klevleev.eskimo.server.core.config.ApplicationSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

	private final ApplicationSettings settings;

	@Autowired
	public FileUtils(ApplicationSettings applicationSettings) {
		this.settings = applicationSettings;
	}

	public File saveFile(MultipartFile file) throws IOException {
		return saveFile(file, "", "");
	}

	public File saveFile(MultipartFile file, String prefix) throws IOException {
		return saveFile(file, prefix, "");
	}

	public File saveFile(MultipartFile file, String prefix, String suffix) throws IOException {
		File filePath = File.createTempFile(prefix, suffix, settings.getTempRoot());
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

	public File getUnzippedContestFolder(File unzipedFile) throws IOException{
		File[] files = unzipedFile.listFiles();
		if (files == null || files.length != 1 || !files[0].isDirectory()) {
			throw new RuntimeException("files in zip have bad format");
		}
		return files[0];
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

	public void deleteFolder(File folder) {
		try {
			org.apache.commons.io.FileUtils.deleteDirectory(folder);
		} catch (Throwable e) {
			logger.error("can't delete temp folder=" + folder, e);
		}
	}

	public void deleteFile(File file) {
		try {
			org.apache.commons.io.FileUtils.forceDelete(file);
		} catch (Throwable e) {
			logger.error("can't delete temp file=" + file, e);
		}
	}

}
