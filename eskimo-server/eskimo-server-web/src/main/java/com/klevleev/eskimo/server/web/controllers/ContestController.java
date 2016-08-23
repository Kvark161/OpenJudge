package com.klevleev.eskimo.server.web.controllers;

import com.klevleev.eskimo.server.core.dao.ContestDao;
import com.klevleev.eskimo.server.core.dao.SubmissionDao;
import com.klevleev.eskimo.server.core.domain.Contest;
import com.klevleev.eskimo.server.core.domain.Problem;
import com.klevleev.eskimo.server.core.domain.Submission;
import com.klevleev.eskimo.server.core.domain.User;
import com.klevleev.eskimo.server.storage.StorageValidationException;
import com.klevleev.eskimo.server.web.viewObjects.UserSubmission;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
public class ContestController {

	private static final Logger logger = LoggerFactory.getLogger(ContestController.class);

	private final ContestDao contestDao;

	private final SubmissionDao submissionDao;

	private final FileUtils fileUtils;

	private final UserUtils userUtils;

	@Autowired
	public ContestController(ContestDao contestDao,
							 SubmissionDao submissionDao,
							 FileUtils fileUtils,
							 UserUtils userUtils) {
		this.contestDao = contestDao;
		this.submissionDao = submissionDao;
		this.fileUtils = fileUtils;
		this.userUtils = userUtils;
	}

	@RequestMapping(value = "/contests", method = RequestMethod.GET)
	public String contests(ModelMap model) {
		List<Contest> contests = contestDao.getAllContests();
		model.addAttribute("currentLocale", userUtils.getCurrentUserLocale());
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

	@RequestMapping(value = "/contest/{contestId}/submissions", method = RequestMethod.GET)
	public String submissions(@PathVariable Long contestId, ModelMap model) {
		Contest contest = contestDao.getContestById(contestId);
		if (contest == null) {
			return "redirect:/contests";
		}
		model.addAttribute("contest", contest);
		List<Submission> submissions = submissionDao.getUserSubmissions(userUtils.getCurrentUser().getId());
		List<Problem> problems = contestDao.getContestById(contestId).getProblems();
		Locale currentLocale = userUtils.getCurrentUserLocale();
		List<UserSubmission> userSubmissions = new ArrayList<>();
		for (Submission submission : submissions) {
			UserSubmission userSubmission = new UserSubmission();
			userSubmission.setSubmissionId(submission.getId());
			userSubmission.setProblemName("DELETED");
			for (Problem problem : problems) {
				if (problem.getId().equals(submission.getProblemId())) {
					userSubmission.setProblemName(problem.getName(currentLocale));
					break;
				}
			}
			userSubmission.setVerdict(submission.getVerdict());
			userSubmissions.add(userSubmission);
		}

		model.addAttribute("userSubmissions", userSubmissions);
		return "contest/submissions";
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
}
