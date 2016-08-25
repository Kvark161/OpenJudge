package com.klevleev.eskimo.server.core.dao;

import com.klevleev.eskimo.server.core.domain.Submission;

import java.util.List;

/**
 * Created by Stepan Klevleev on 15-Aug-16.
 */
public interface SubmissionDao {

	List<Submission> getAllSubmissions();

	Submission getSubmissionById(Long id);

	List<Submission> getUserSubmissions(Long userId);

	List<Submission> getUserInContestSubmissions(Long userId, Long contestId);

	void insertSubmission(Submission submission);

	void updateSubmission(Submission submission);

}
