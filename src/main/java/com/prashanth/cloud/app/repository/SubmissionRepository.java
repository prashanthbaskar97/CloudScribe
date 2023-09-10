package com.prashanth.cloud.app.repository;

import com.hari.cloud.app.dao.Submission;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SubmissionRepository extends CrudRepository<Submission, String> {
    List<Submission> findByAssignmentId(String assignmentId);
}