package com.klevleev.eskimo.server.web.utils;

import com.klevleev.eskimo.server.core.dao.UserDao;
import com.klevleev.eskimo.server.core.domain.User;
import com.klevleev.eskimo.server.web.controllers.ContestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;


/**
 * Created by Ekaterina Sokirkina on 23.08.2016.
 */
@Component
public class UserUtils {

	private static final Logger logger = LoggerFactory.getLogger(UserUtils.class);

	private final UserDao userDao;

	@Autowired
	public UserUtils(UserDao userDao) {
		this.userDao = userDao;
	}

	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return (User)authentication.getPrincipal();
	}

	public Locale getCurrentUserLocale(){
		User currentUser = getCurrentUser();
		return currentUser == null ? new Locale("en") : currentUser.getLocale();
	}
}
