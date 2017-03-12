package com.klevleev.eskimo.server.web.utils;

import com.klevleev.eskimo.server.core.config.ApplicationSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

	private void deleteFile(File file){
		try {
			org.apache.commons.io.FileUtils.forceDelete(file);
		} catch (Throwable e) {
			logger.error("can't delete temp file=" + file, e);
		}
	}

}
