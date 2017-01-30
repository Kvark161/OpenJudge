package com.klevleev.eskimo.server.storage.utils;

import com.klevleev.eskimo.server.storage.StorageException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Created by Sokirkina Ekaterina on 26-Jan-2017.
 */
public class ParseUtils {

	private ParseUtils() {
	}

	//TODO 1 test if filename exists in config file, but doesn't exist in folder
	public static List<File> parseJSONArray(Reader json, File folder){
		try {
			JSONParser parser = new JSONParser();
			JSONArray list = (JSONArray) parser.parse(json);
			List<File> files  = new ArrayList<>();
			for (Object s : list){
				JSONObject object = (JSONObject)s;
				File nextFile = new File(folder + File.separator + object.get("name"));
				if (!nextFile.exists()){
					throw new StorageException("file "+ object.get("name") +" does not exists");
				}
				files.add(nextFile);
			}
			return files;
		} catch (IOException e) {
			throw new StorageException(e);
		} catch (ParseException|StorageException|ClassCastException e){
			throw new StorageException("error in parsing json array", e);
		}
	}

	public static List<File> parseJSONObject(Reader json, File folder){
		try {
			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject) parser.parse(json);
			File nextFile = new File(folder + File.separator + object.get("name"));
			if (!nextFile.exists()) {
				throw new StorageException("file " + object.get("name") + " does not exists");
			}
			List<File> result = new ArrayList<>();
			result.add(nextFile);
			return result;
		} catch (IOException e) {
			throw new StorageException(e);
		} catch (ParseException|StorageException|ClassCastException e){
			throw new StorageException("error in parsing json object ", e);
		}
	}

	public static void parseToStorageFiles(File sourceFolder, File jsonFile, File destinationFolder, boolean isOneFile){
		try {
			//noinspection ResultOfMethodCallIgnored
			destinationFolder.mkdirs();
			try(Reader jsonReader = new FileReader(jsonFile)) {
				List<File> files = isOneFile ? parseJSONObject(jsonReader, sourceFolder) :
						                       parseJSONArray(jsonReader, sourceFolder);
				Files.copy(jsonFile.toPath(),
						new File(destinationFolder + File.separator + jsonFile.getName()).toPath());
				copyAll(files, destinationFolder);
			}
		} catch (IOException|StorageException e){
			throw new StorageException("cannot parse to storage files from " + sourceFolder.getPath() + " to " +
				destinationFolder.getPath(), e);
		}
	}


	public static <T> List<T> parseArray(File folder, File json, BiFunction<JSONObject,  File, T> f){
		try {
			JSONParser parser = new JSONParser();
			try (Reader r = new FileReader(json)) {
				JSONArray list = (JSONArray) parser.parse(r);
				List<T> result = new ArrayList<>();
				for (Object object : list) {
					JSONObject jsonObject = (JSONObject) object;
					result.add(f.apply(jsonObject, folder));
				}
				return result;
			}
		} catch (Throwable e) {
			throw new StorageException("cannot parse array", e);
		}
	}

	public static <T> T parseObject(File folder, File json, BiFunction<JSONObject,  File, T> f){
		try {
			JSONParser parser = new JSONParser();
			try (Reader r = new FileReader(json)) {
				JSONObject jsonObject = (JSONObject) parser.parse(r);
				return f.apply(jsonObject, folder);
			}
		} catch (Throwable e) {
			throw new StorageException("cannot parse object", e);
		}
	}

	public static void copyAll(List<File> files, File destinationFolder){
		//noinspection ResultOfMethodCallIgnored
		destinationFolder.mkdirs();
		try {
			for (File f : files) {
				Assert.state(f.exists(), "FILE DOESN'T EXIST");
				Files.copy(f.toPath(),
						new File(destinationFolder + File.separator + f.getName()).toPath());
			}
		} catch (IOException e){
			throw new StorageException("error while copy files to " + destinationFolder, e);
		}
	}
}
