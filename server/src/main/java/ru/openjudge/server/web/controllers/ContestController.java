package ru.openjudge.server.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/contest/{contestId}")
public class ContestController {

    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public String getWelcome(@PathVariable Long contestId, ModelMap model) {
        model.addAttribute("message", "I am contest #" + contestId);
        return "contestSummary";
    }
}
