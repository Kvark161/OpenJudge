package com.klevleev.eskimo.backend.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * Created by Sokirkina Ekaterina on 02-Feb-2017.
 */
@Slf4j
public class TemporaryFile implements Closeable {

	@Getter
	private File file;

	public TemporaryFile(File file) {
		if (!file.isFile()) {
			log.warn(file + "is not a file");
		}
		this.file = file;
	}

	@Override
	public void close() throws IOException {
		try {
			org.apache.commons.io.FileUtils.forceDelete(file);
		} catch (Throwable e) {
			log.error("can't delete temp file=" + file, e);
		}
	}
}
