package ru.openjudge.server.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class WelcomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getWelcome(ModelMap model) {
        model.addAttribute("message", "Welcome!");
        return "welcome";
    }

    @RequestMapping(value = "/contests", method = RequestMethod.GET)
    public String getContests(ModelMap model) {
        return "contests";
    }

}
