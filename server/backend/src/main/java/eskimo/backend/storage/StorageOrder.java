package eskimo.backend.storage;

public abstract class StorageOrder {

    abstract void execute() throws StorageOrderException;

    abstract void rollback();
}
