package com.klevleev.eskimo.server.web.controllers;

import com.klevleev.eskimo.server.core.domain.Contest;
import com.klevleev.eskimo.server.core.domain.Submission;
import com.klevleev.eskimo.server.core.domain.User;
import com.klevleev.eskimo.server.core.services.ContestService;
import com.klevleev.eskimo.server.core.services.SubmissionService;
import com.klevleev.eskimo.server.core.services.UserService;
import com.klevleev.eskimo.server.web.forms.EditContestForm;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Controller
public class ContestController {

	private static final Logger logger = LoggerFactory.getLogger(ContestController.class);

	private final UserService userService;

	private final FileUtils fileUtils;

	private final UserUtils userUtils;

	private final SubmissionService submissionService;

	private final ContestService contestService;

	@Autowired
	public ContestController(UserService userService,
	                         FileUtils fileUtils,
	                         UserUtils userUtils,
	                         SubmissionService submissionService,
	                         ContestService contestService) {
		this.userService = userService;
		this.fileUtils = fileUtils;
		this.userUtils = userUtils;
		this.submissionService = submissionService;
		this.contestService = contestService;
	}

	@GetMapping(value = "/contests")
	public String contests(ModelMap model) {
		List<Contest> contests = contestService.getAllContests();
		model.addAttribute("currentLocale", userUtils.getCurrentUserLocale());
		model.addAttribute("contests", contests);
		return "contests";
	}

	@GetMapping(value = "/contest/{contestId}")
	public String summary(@PathVariable Long contestId, ModelMap model) {
		Contest contest = contestService.getContestById(contestId);
		if (contest == null) {
			return "redirect:/contests";
		}
		model.addAttribute("contest", contest);
		return "contest/summary";
	}

	@GetMapping(value = "/contest/{contestId}/problems")
	public String problems(@PathVariable Long contestId, ModelMap model) {
		Contest contest = contestService.getContestById(contestId);
		if (contest == null) {
			return "redirect:/contests";
		}
		model.addAttribute("contest", contest);
		return "contest/problems";
	}

	@GetMapping(value = "/contest/{contestId}/submit")
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

	@PostMapping(value = "/contest/{contestId}/submit")
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
		submission.setSendingDateTime(LocalDateTime.now());
		submission.setContest(contestService.getContestById(contestId));
		submission.setProblem(contestService.getContestProblem(contestId, submissionForm.getProblemId()));
		submission.setSourceCode(submissionForm.getSourceCode());
		submission.setUser(userService.getUserById(user.getId()));
		submissionService.submit(submission);
		return "redirect:/contest/{contestId}/submissions";
	}

	@GetMapping(value = "/contest/{contestId}/submissions")
	public String submissions(@PathVariable Long contestId,
	                          ModelMap model,
	                          @AuthenticationPrincipal User user) {
		Contest contest = contestService.getContestById(contestId);
		if (contest == null) {
			return "redirect:/contests";
		}
		model.addAttribute("contest", contest);
		List<Submission> submissions = submissionService.getUserSubmissions(user.getId(), contestId);
		Locale currentLocale = user.getLocale();
		model.addAttribute("submissions", submissions);
		model.addAttribute("locale", currentLocale);
		return "contest/submissions";
	}

	@GetMapping(value = "/contest/{contestId}/standings")
	public String standings(@PathVariable Long contestId, ModelMap model) {
		Contest contest = contestService.getContestById(contestId);
		if (contest == null) {
			return "redirect:/contests";
		}
		model.addAttribute("contest", contest);
		return "contest/standings";
	}

	@GetMapping(value = "/contests/new")
	public String newContest() {
		return "contest/new";
	}

	@PostMapping(value = "/contests/new/zip")
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

	@GetMapping(value = "/contest/{contestId}/edit")
	public String editContest(@PathVariable Long contestId, ModelMap model) {
		Contest contest = contestService.getContestById(contestId);
		if (contest == null) {
			return "redirect:/contests";
		}
		model.addAttribute("contest", contest);
		EditContestForm editContestForm = new EditContestForm(contest);
		model.addAttribute("editContestForm", editContestForm);
		return "contest/edit";
	}

	@PostMapping(value = "/contest/{contestId}/edit")
	public String editContest(@PathVariable Long contestId,
	                          @Valid @ModelAttribute("editContestForm") EditContestForm editContestForm,
	                          BindingResult bindingResult,
	                          ModelMap model) {
		Contest contest = contestService.getContestById(contestId);
		if (contest == null) {
			return "redirect:/contests";
		}
		model.addAttribute("contest", contest);
		if (bindingResult.hasErrors()) {
			return "contest/edit";
		}
		return "redirect:/contest/{contestId}";
	}

}
