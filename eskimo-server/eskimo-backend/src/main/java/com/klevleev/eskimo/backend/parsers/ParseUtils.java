package com.klevleev.eskimo.backend.parsers;


import com.klevleev.eskimo.backend.exceptions.ContestParseException;

import java.io.File;

/**
 * Created by Sokirkina Ekaterina on 09-Feb-2017.
 */
public final class ParseUtils {

	public static File getFile(File directory, String fileName){
		File file = new File(directory + File.separator + fileName);
		if (!file.exists())
			throw new ContestParseException("file " + file.getName() + " doesn't exist");
		return file;
	}
}
