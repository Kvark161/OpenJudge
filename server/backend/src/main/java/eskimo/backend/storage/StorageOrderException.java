package eskimo.backend.storage;

@SuppressWarnings("WeakerAccess")
public class StorageOrderException extends RuntimeException {

    public StorageOrderException() {
    }

    public StorageOrderException(String message) {
        super(message);
    }

    public StorageOrderException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageOrderException(Throwable cause) {
        super(cause);
    }

    public StorageOrderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
