package com.klevleev.eskimo.server.storage;

/**
 * Created by Stepan Klevleev on 25-Jul-16.
 */
@SuppressWarnings("WeakerAccess")
public class StorageValidationException extends Exception {

	public StorageValidationException() {
	}

	public StorageValidationException(String message) {
		super(message);
	}

	public StorageValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public StorageValidationException(Throwable cause) {
		super(cause);
	}

}
