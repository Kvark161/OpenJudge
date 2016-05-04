package ru.openjudge.server.entity;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "PROBLEMS")
public class Problem implements Serializable {

    private static final long serialVersionUID = -321080827466439441L;

    @Id
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "CONTEST_ID", nullable = false)
    private Contest contest;

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public Contest getContest() {
        return contest;
    }

    public void setContest(Contest contest) {
        this.contest = contest;
    }
}
