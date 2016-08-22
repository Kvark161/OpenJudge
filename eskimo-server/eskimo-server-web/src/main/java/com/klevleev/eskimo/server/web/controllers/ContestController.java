package com.klevleev.eskimo.server.web.controllers;

import com.klevleev.eskimo.server.core.dao.ContestDao;
import com.klevleev.eskimo.server.core.dao.SubmissionDao;
import com.klevleev.eskimo.server.core.dao.UserDao;
import com.klevleev.eskimo.server.core.domain.Contest;
import com.klevleev.eskimo.server.core.domain.Submission;
import com.klevleev.eskimo.server.core.domain.User;
import com.klevleev.eskimo.server.storage.StorageValidationException;
import com.klevleev.eskimo.server.web.forms.SubmissionForm;
import com.klevleev.eskimo.server.web.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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

	private final ContestDao contestDao;

	private final SubmissionDao submissionDao;

	private final UserDao userDao;

	private final FileUtils fileUtils;

	@Autowired
	public ContestController(ContestDao contestDao, SubmissionDao submissionDao, UserDao userDao, FileUtils fileUtils) {
		this.contestDao = contestDao;
		this.submissionDao = submissionDao;
		this.userDao = userDao;
		this.fileUtils = fileUtils;
	}

	@RequestMapping(value = "/contests", method = RequestMethod.GET)
	public String contests(ModelMap model) {
		List<Contest> contests = contestDao.getAllContests();
		model.addAttribute("currentLocale", getCurrentUserLocale());
		model.addAttribute("contests", contests);
		return "contests";
	}

	@RequestMapping(value = "/contest/{contestId}", method = RequestMethod.GET)
	public String summary(@PathVariable Long contestId, ModelMap model) {
		Contest contest = contestDao.getContestById(contestId);
		if (contest == null) {
			return "redirect:/contests";
		}
		model.addAttribute("contest", contest);
		return "contest/summary";
	}

	@RequestMapping(value = "/contest/{contestId}/problems", method = RequestMethod.GET)
	public String problems(@PathVariable Long contestId, ModelMap model) {
		Contest contest = contestDao.getContestById(contestId);
		if (contest == null) {
			return "redirect:/contests";
		}
		model.addAttribute("contest", contest);
		return "contest/problems";
	}

	@RequestMapping(value = "/contest/{contestId}/submit", method = RequestMethod.GET)
	public String submit(@PathVariable Long contestId, ModelMap model) {
		Contest contest = contestDao.getContestById(contestId);
		if (contest == null) {
			return "redirect:/contests";
		}
		model.addAttribute("currentLocale", getCurrentUserLocale());
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
		Contest contest = contestDao.getContestById(contestId);
		if (contest == null) {
			return "redirect:/contests";
		}
		model.addAttribute("contest", contest);
		if (bindingResult.hasErrors()) {
			return "contest/submit";
		}
		Submission submission = new Submission();
		submission.setContestId(contestId);
		submission.setProblemId(submissionForm.getProblemId());
		submission.setSourceCode(submissionForm.getSourceCode());
		submission.setUserId(user.getId());
		submissionDao.insertSubmission(submission);
		return "redirect:/contest/{contestId}/submit";
	}

	@RequestMapping(value = "/contest/{contestId}/standings", method = RequestMethod.GET)
	public String standings(@PathVariable Long contestId, ModelMap model) {
		Contest contest = contestDao.getContestById(contestId);
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
	public
	@ResponseBody
	String uploadFileHandler(@RequestParam("file") MultipartFile multipartFile) {
		try {
			File contestZipFile = fileUtils.saveFile(multipartFile);
			File contestFolder = fileUtils.unzip(contestZipFile);
			contestDao.insertContest(contestFolder);
		} catch (IOException e) {
			logger.error("can not upload file " + multipartFile.getName(), e);
			return "can not upload file";
		} catch (StorageValidationException e) {
			logger.info("can not parse contest " + multipartFile.getName(), e);
			return "file is invalid";
		}
		return "redirect:/contests";
	}

	private Locale getCurrentUserLocale(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = userDao.getUserByName(authentication.getName());
		return currentUser == null? new Locale("en") : currentUser.getLocale();
	}
}
