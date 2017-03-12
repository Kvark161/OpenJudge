package com.klevleev.eskimo.server.core.enums;

/**
 * Created by Sokirkina Ekaterina on 03-Feb-2017.
 */
public enum ProgrammingLanguage {
	GPLUSPLUS;

	public static ProgrammingLanguage getValue(String s){
		if (s.equals("g++"))
			return GPLUSPLUS;
		throw new IllegalArgumentException("Incorrect programming language: " + s);
	}
}
