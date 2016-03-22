package ru.openjudge.server.entity;

import java.io.Serializable;

public class Problem implements Serializable {

    private static final long serialVersionUID = -1942757296232795347L;

    private long id;

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }
}
