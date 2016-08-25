package com.klevleev.eskimo.server.core.services;

import com.klevleev.eskimo.server.core.domain.Submission;

import java.util.List;

/**
 * Created by Stepan Klevleev on 23-Aug-16.
 */
public interface SubmissionService {

	void submit(Submission submission);

	List<Submission> getAllSubmissions();

	Submission getSubmissionById(Long id);

	List<Submission> getUserSubmissions(Long userId);

	List<Submission> getUserInContestSubmissions(Long userId, Long contestId);

}
