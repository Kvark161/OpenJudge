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
public class TemplateFile implements Closeable {

	private static final Logger logger = LoggerFactory.getLogger(TemplateFile.class);

	@Getter
	private File file;

	public TemplateFile(File file) {
		if (!file.isFile())
			logger.error(file + "is not a file");
		this.file = file;
	}

	@Override
	public void close() throws IOException {
		try {
			org.apache.commons.io.FileUtils.forceDelete(file);
		} catch (Throwable e) {
			logger.error("can't delete temp file=" + file, e);
		}
	}
}
