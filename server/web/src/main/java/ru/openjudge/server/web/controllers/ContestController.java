package ru.openjudge.server.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.openjudge.server.datalayer.dao.ContestDao;
import ru.openjudge.server.datalayer.domain.Contest;

import java.util.List;

@Controller
public class ContestController {

	private static final Logger logger = LoggerFactory.getLogger(ContestController.class);

	@Autowired
	private ContestDao contestDao;

	@RequestMapping(value = "/contests", method = RequestMethod.GET)
	public String contests(ModelMap model) {
		List<Contest> contests = contestDao.getAllContests();
		model.addAttribute("contests", contests);
		return "contests";
	}

	@RequestMapping(value = "/contest/{contestId}", method = RequestMethod.GET)
	public String getWelcome(@PathVariable Long contestId, ModelMap model) {
		model.addAttribute("message", "I am contest #" + contestId);
		return "contest/root";
	}
}
