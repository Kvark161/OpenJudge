package com.klevleev.eskimo.server.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;

@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@GetMapping(value = "/")
	public String welcome(ModelMap model, Locale locale) {
		String lan = locale.getLanguage();
		return "home";
	}

	@GetMapping(value = "/login")
	public String login(@RequestParam(value = "error", required = false) String error,
	                    @RequestParam(value = "logout", required = false) String logout,
	                    ModelMap model,
	                    Authentication authentication) {
		if (authentication != null) {
			return "redirect:/";
		}
		if (error != null) {
			model.addAttribute("error", "Invalid username or password!");
		}
		if (logout != null) {
			model.addAttribute("msg", "You've been logged out successfully.");
		}
		return "login";
	}

	@GetMapping(value = "/signup")
	public String login(ModelMap model,
	                    Authentication authentication) {
		if (authentication != null) {
			return "redirect:/";
		}
		return "signup";
	}

}
