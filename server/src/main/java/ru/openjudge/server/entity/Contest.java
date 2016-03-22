package ru.openjudge.server.entity;

import java.io.Serializable;

public class Contest implements Serializable {

    private static final long serialVersionUID = 5657856422377586257L;

    Long id;

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

}
