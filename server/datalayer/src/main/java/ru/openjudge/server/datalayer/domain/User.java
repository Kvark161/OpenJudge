package ru.openjudge.server.datalayer.domain;

import java.io.Serializable;

/**
 * Created by Stepan Klevleev on 27-Jul-16.
 */
public class User implements Serializable {
	private static final long serialVersionUID = 291660316680943555L;

	private Long id;
	private String username;
	private String password;
	private boolean isAdmin;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String name) {
		this.username = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean admin) {
		isAdmin = admin;
	}
}
