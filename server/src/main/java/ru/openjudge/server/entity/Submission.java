package ru.openjudge.server.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "SUBMISSIONS")
public class Submission implements Serializable {

    private static final long serialVersionUID = -5547448500158392136L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
