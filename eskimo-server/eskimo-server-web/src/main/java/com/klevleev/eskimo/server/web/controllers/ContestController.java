package com.klevleev.eskimo.server.web.controllers;

import com.klevleev.eskimo.server.core.dao.ContestDao;
import com.klevleev.eskimo.server.core.domain.Contest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class ContestController {

	private static final Logger logger = LoggerFactory.getLogger(ContestController.class);

	private final ContestDao contestDao;

	@Autowired
	public ContestController(ContestDao contestDao) {
		assert contestDao != null;
		this.contestDao = contestDao;
	}

	@RequestMapping(value = "/contests", method = RequestMethod.GET)
	public String contests(ModelMap model) {
		List<Contest> contests = contestDao.getAllContests();
		model.addAttribute("contests", contests);
		return "contests";
	}

	@RequestMapping(value = "/contest/{contestId}", method = RequestMethod.GET)
	public String summary(@PathVariable Long contestId, ModelMap model) {
		Contest contest = contestDao.getContestById(contestId);
		if (contest == null) {
			return "redirect:contests";
		}
		model.addAttribute("contest", contest);
		return "contest/summary";
	}

	@RequestMapping(value = "/contest/{contestId}/problems", method = RequestMethod.GET)
	public String problems(@PathVariable Long contestId, ModelMap model) {
		Contest contest = contestDao.getContestById(contestId);
		if (contest == null) {
			return "redirect:contests";
		}
		model.addAttribute("contest", contest);
		return "contest/problems";
	}

	@RequestMapping(value = "/contest/{contestId}/submit", method = RequestMethod.GET)
	public String submit(@PathVariable Long contestId, ModelMap model) {
		Contest contest = contestDao.getContestById(contestId);
		if (contest == null) {
			return "redirect:contests";
		}
		model.addAttribute("contest", contest);
		return "contest/submit";
	}

	@RequestMapping(value = "/contest/{contestId}/standings", method = RequestMethod.GET)
	public String standings(@PathVariable Long contestId, ModelMap model) {
		Contest contest = contestDao.getContestById(contestId);
		if (contest == null) {
			return "redirect:contests";
		}
		model.addAttribute("contest", contest);
		return "contest/standings";
	}

}
