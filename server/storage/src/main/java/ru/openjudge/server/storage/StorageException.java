package ru.openjudge.server.storage;

/**
 * Created by Stepan Klevleev on 25-Jul-16.
 */
@SuppressWarnings("WeakerAccess")
public class StorageException extends RuntimeException {

	public StorageException() {
		super();
	}

	public StorageException(String message) {
		super(message);
	}

	public StorageException(String message, Throwable e) {
		super(message, e);
	}

	public StorageException(Throwable e) {
		super(e);
	}

}
