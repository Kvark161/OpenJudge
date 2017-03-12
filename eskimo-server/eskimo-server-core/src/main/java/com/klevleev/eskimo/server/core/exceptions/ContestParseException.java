package com.klevleev.eskimo.server.core.exceptions;

/**
 * Created by Sokirkina Ekaterina on 09-Feb-2017.
 */
public class ContestParseException extends RuntimeException {

	public ContestParseException() {
		super();
	}

	public ContestParseException(String message) {
		super(message);
	}

	public ContestParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContestParseException(Throwable cause) {
		super(cause);
	}

	public static String getWrongJSONFormatMessage(String jsonObjectName, String fieldName){
		return jsonObjectName + " json object has wrong format: " + fieldName + " doesn't exists";
	}
}
