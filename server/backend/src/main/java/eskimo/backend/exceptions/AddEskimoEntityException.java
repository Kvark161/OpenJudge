package eskimo.backend.exceptions;

public class AddEskimoEntityException extends RuntimeException {

    public AddEskimoEntityException(String message) {
        super(message);
    }

    public AddEskimoEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
