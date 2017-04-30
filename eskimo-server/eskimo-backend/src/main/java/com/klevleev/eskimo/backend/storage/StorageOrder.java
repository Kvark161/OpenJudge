package com.klevleev.eskimo.backend.storage;

/**
 * Created by Stepan Klevleev on 30-Apr-17.
 */
public abstract class StorageOrder {

    abstract void execute() throws StorageOrderException;

    abstract void rollback();
}
