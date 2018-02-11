package com.klevleev.eskimo.backend.exceptions;

public class CreateContestException extends RuntimeException {

    public CreateContestException(String message) {
        super(message);
    }

    public CreateContestException(String message, Throwable cause) {
        super(message, cause);
    }
}
