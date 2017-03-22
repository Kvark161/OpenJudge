package com.klevleev.eskimo.backend.services;

import com.klevleev.eskimo.backend.domain.Submission;

import java.util.List;

/**
 * Created by Stepan Klevleev on 23-Aug-16.
 */
public interface SubmissionService {

	void submit(Submission submission);

	List<Submission> getAllSubmissions();

	Submission getSubmissionById(Long id);

	List<Submission> getUserSubmissions(Long userId);

	List<Submission> getUserSubmissions(Long userId, Long contestId);

}
