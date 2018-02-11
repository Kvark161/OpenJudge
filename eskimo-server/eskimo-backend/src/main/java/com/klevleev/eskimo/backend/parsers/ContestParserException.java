package com.klevleev.eskimo.backend.parsers;

/**
 * Created by Sokirkina Ekaterina on 09-Feb-2017.
 */
public class ContestParserException extends RuntimeException {

    public ContestParserException() {
        super();
    }

    public ContestParserException(String message) {
        super(message);
    }

    public ContestParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContestParserException(Throwable cause) {
        super(cause);
    }

    public static String getWrongJSONFormatMessage(String jsonObjectName, String fieldName) {
        return jsonObjectName + " json object has wrong format: " + fieldName + " doesn't exist";
    }
}
