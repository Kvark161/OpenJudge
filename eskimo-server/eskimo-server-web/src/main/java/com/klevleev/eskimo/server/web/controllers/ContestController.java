package com.klevleev.eskimo.server.web.controllers;

import com.klevleev.eskimo.server.core.dao.SubmissionDao;
import com.klevleev.eskimo.server.core.dao.UserDao;
import com.klevleev.eskimo.server.core.domain.Contest;
import com.klevleev.eskimo.server.core.domain.Submission;
import com.klevleev.eskimo.server.core.domain.User;
import com.klevleev.eskimo.server.core.services.ContestService;
import com.klevleev.eskimo.server.core.services.SubmissionService;
import com.klevleev.eskimo.server.web.forms.SubmissionForm;
import com.klevleev.eskimo.server.web.utils.FileUtils;
import com.klevleev.eskimo.server.web.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Controller
public class ContestController {

	private static final Logger logger = LoggerFactory.getLogger(ContestController.class);

	private final UserDao userDao;

	private final SubmissionDao submissionDao;

	private final FileUtils fileUtils;

	private final UserUtils userUtils;

	private final SubmissionService submissionService;

	private final ContestService contestService;

	@Autowired
	public ContestController(UserDao userDao,
							 SubmissionDao submissionDao,
							 FileUtils fileUtils,
							 UserUtils userUtils,
							 SubmissionService submissionService,
							 ContestService contestService) {
		this.userDao = userDao;
		this.submissionDao = submissionDao;
		this.fileUtils = fileUtils;
		this.userUtils = userUtils;
		this.submissionService = submissionService;
		this.contestService = contestService;
	}

	@RequestMapping(value = "/contests", method = RequestMethod.GET)
	public String contests(ModelMap model) {
		List<Contest> contests = contestService.getAllContests();
		model.addAttribute("currentLocale", userUtils.getCurrentUserLocale());
		model.addAttribute("contests", contests);
		return "contests";
	}

	@RequestMapping(value = "/contest/{contestId}", method = RequestMethod.GET)
	public String summary(@PathVariable Long contestId, ModelMap model) {
		Contest contest = contestService.getContestById(contestId);
		if (contest == null) {
			return "redirect:/contests";
		}
		model.addAttribute("contest", contest);
		return "contest/summary";
	}

	@RequestMapping(value = "/contest/{contestId}/problems", method = RequestMethod.GET)
	public String problems(@PathVariable Long contestId, ModelMap model) {
		Contest contest = contestService.getContestById(contestId);
		if (contest == null) {
			return "redirect:/contests";
		}
		model.addAttribute("contest", contest);
		return "contest/problems";
	}

	@RequestMapping(value = "/contest/{contestId}/submit", method = RequestMethod.GET)
	public String submit(@PathVariable Long contestId, ModelMap model) {
		Contest contest = contestService.getContestById(contestId);
		if (contest == null) {
			return "redirect:/contests";
		}
		model.addAttribute("currentLocale", userUtils.getCurrentUserLocale());
		model.addAttribute("submissionForm", new SubmissionForm());
		model.addAttribute("contest", contest);
		return "contest/submit";
	}

	@RequestMapping(value = "/contest/{contestId}/submit", method = RequestMethod.POST)
	public String submit(@PathVariable Long contestId,
						 @Valid @ModelAttribute("submissionForm") SubmissionForm submissionForm,
						 BindingResult bindingResult,
						 @AuthenticationPrincipal User user,
						 Model model) {
		Contest contest = contestService.getContestById(contestId);
		if (contest == null) {
			return "redirect:/contests";
		}
		model.addAttribute("contest", contest);
		if (bindingResult.hasErrors()) {
			return "contest/submit";
		}
		Submission submission = new Submission();
		submission.setContest(contestService.getContestById(contestId));
		submission.setProblem(contestService.getProblemByContestAndProblemId(contestId, submissionForm.getProblemId()));
		submission.setSourceCode(submissionForm.getSourceCode());
		submission.setUser(userDao.getUserById(user.getId()));
		submissionService.submit(submission);
		return "redirect:/contest/{contestId}/submissions";
	}

	@RequestMapping(value = "/contest/{contestId}/submissions", method = RequestMethod.GET)
	public String submissions(@PathVariable Long contestId,
	                          ModelMap model,
	                          @AuthenticationPrincipal User user) {
		Contest contest = contestService.getContestById(contestId);
		if (contest == null) {
			return "redirect:/contests";
		}
		model.addAttribute("contest", contest);
		List<Submission> submissions = submissionService.getUserInContestSubmissions(user.getId(), contestId);
		Locale currentLocale = user.getLocale();
		model.addAttribute("submissions", submissions);
		model.addAttribute("locale", currentLocale);
		return "contest/submissions";
	}

	@RequestMapping(value = "/contest/{contestId}/standings", method = RequestMethod.GET)
	public String standings(@PathVariable Long contestId, ModelMap model) {
		Contest contest = contestService.getContestById(contestId);
		if (contest == null) {
			return "redirect:/contests";
		}
		model.addAttribute("contest", contest);
		return "contest/standings";
	}

	@RequestMapping(value = "/contests/new", method = RequestMethod.GET)
	public String newContest() {
		return "contest/new";
	}

	@RequestMapping(value = "/contests/new/zip", method = RequestMethod.POST)
	public String newContestFromZip(@RequestParam("file") MultipartFile multipartFile,
	                         Model model) {
		File contestZipFile = null;
		File contestFolder = null;
		try {
			contestZipFile = fileUtils.saveFile(multipartFile, "contest-", "zip");
			contestFolder = fileUtils.unzip(contestZipFile);
			contestService.createContest(contestFolder);
		} catch (IOException e) {
			logger.error("can not upload file " + multipartFile.getName(), e);
			model.addAttribute("error", "can't upload a file: " + e.getMessage());
			return "/contest/new";
		} catch (Throwable e) {
			logger.debug("incorrect contest's format", e);
			model.addAttribute("error", "incorrect contest's format: " + e.getMessage());
			return "/contest/new";
		} finally {
			if (contestFolder != null) {
				fileUtils.deleteFolder(contestFolder);
			}
			if (contestZipFile != null) {
				fileUtils.deleteFile(contestZipFile);
			}
		}
		return "redirect:/contests";
	}
}
