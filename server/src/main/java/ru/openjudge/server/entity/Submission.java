package ru.openjudge.server.entity;

import java.io.Serializable;

public class Submission implements Serializable {

    private static final long serialVersionUID = 6160046873027290025L;

    private long id;

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }
}
