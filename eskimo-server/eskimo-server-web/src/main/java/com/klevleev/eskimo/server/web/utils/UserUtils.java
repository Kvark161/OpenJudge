package com.klevleev.eskimo.server.web.utils;

import com.klevleev.eskimo.server.core.config.ApplicationSettings;
import com.klevleev.eskimo.server.core.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;


/**
 * Created by Ekaterina Sokirkina on 23.08.2016.
 */
@Component("userUtils")
public class UserUtils {

	private static final Logger logger = LoggerFactory.getLogger(UserUtils.class);

	private ApplicationSettings applicationSettings;

	@Autowired
	public UserUtils(ApplicationSettings applicationSettings) {
		this.applicationSettings = applicationSettings;
	}

	public Locale getCurrentUserLocale() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return new Locale(applicationSettings.getDefaultLanguage());
        }
        User user = (User) authentication.getPrincipal();
        	return user.getLocale();
	}
}
