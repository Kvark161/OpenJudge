package com.klevleev.eskimo.server.security;

import com.klevleev.eskimo.server.core.dao.UserDao;
import com.klevleev.eskimo.server.core.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Stepan Klevleev on 27-Jul-16.
 */
@Component("authenticationService")
public class AuthenticationService implements AuthenticationProvider {

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

	private UserDao userDao;

	@Autowired
	public AuthenticationService(UserDao userDao) {
		Assert.notNull(userDao, "userDao is null in AuthenticationService");
		this.userDao = userDao;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String name = authentication.getName();
		String password = (String) authentication.getCredentials();
		User user = userDao.getUserByName(name);
		if (user == null) {
			throw new BadCredentialsException("User not found.");
		}
		if (password.equals(user.getPassword())) {
			Collection<GrantedAuthority> autorities = new ArrayList<>();
			autorities.add(new SimpleGrantedAuthority("ROLE_USER"));
			if (user.isAdmin()) {
				autorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
			}
			return new UsernamePasswordAuthenticationToken(user, password, autorities);
		} else {
			throw new BadCredentialsException("Wrong password.");
		}
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return true;
	}
}
