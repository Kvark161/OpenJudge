package ru.openjudge.server.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "CONTESTS")
public class Contest implements Serializable {

    private static final long serialVersionUID = 5563099537254202778L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private long id;

    @Column(name = "NAME", unique = true, nullable = false)
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
