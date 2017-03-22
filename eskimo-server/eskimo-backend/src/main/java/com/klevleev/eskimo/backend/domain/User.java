package com.klevleev.eskimo.backend.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by Stepan Klevleev on 27-Jul-16.
 */
@Data
public class User implements Serializable {
	private static final long serialVersionUID = 291660316680943555L;

	private Long id;
	private String username;
	private String password;
	private boolean isAdmin;
	private Locale locale;

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", username='" + username + '\'' +
				", isAdmin=" + isAdmin +
				", locale=" + locale +
				'}';
	}
}
