package ru.openjudge.server.entity;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 1039048539907870717L;

    Long id;

    String login;

    String password;

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
