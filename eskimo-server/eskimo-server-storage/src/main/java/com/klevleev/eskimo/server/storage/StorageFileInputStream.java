package com.klevleev.eskimo.server.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Sokirkina Ekaterina on 25-Sep-2016.
 */
public class StorageFileInputStream extends FileInputStream {
	public StorageFileInputStream(File file) throws FileNotFoundException {
		super(file);
	}
}
