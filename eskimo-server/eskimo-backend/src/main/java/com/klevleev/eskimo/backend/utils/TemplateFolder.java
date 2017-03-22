package com.klevleev.eskimo.backend.utils;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * Created by Sokirkina Ekaterina on 02-Feb-2017.
 */
public class TemplateFolder implements Closeable {

	private static final Logger logger = LoggerFactory.getLogger(TemplateFolder.class);

	@Getter
	private File folder;

	public TemplateFolder(File folder) {
		if (!folder.isDirectory())
			logger.error(folder + "is not a folder");
		this.folder = folder;
	}

	@Override
	public void close() throws IOException {
		try {
			org.apache.commons.io.FileUtils.deleteDirectory(folder);
		} catch (Throwable e) {
			logger.error("can't delete temp folder=" + folder, e);
		}
	}
}
