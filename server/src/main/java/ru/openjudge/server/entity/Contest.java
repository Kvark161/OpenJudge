package ru.openjudge.server.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "CONTESTS")
public class Contest implements Serializable {

    private static final long serialVersionUID = 4883847832670186483L;

    @Id
    @Column(name = "ID")
    private long id;

    @Column(name = "NAME", unique = true)
    private String name;

    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
