package com.prashanth.cloud.app.service;

import com.hari.cloud.app.dao.Assignment;
import com.hari.cloud.app.dao.Submission;
import com.hari.cloud.app.dao.User;
import com.hari.cloud.app.dto.AssignmentDto;
import com.hari.cloud.app.dto.SubmissionDto;
import com.hari.cloud.app.repository.AssignmentRepository;
import com.hari.cloud.app.repository.SubmissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class SubmissionService {
    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    public Submission createSubmission(SubmissionDto submissionDto, String assignmentId) throws PSQLException {
        log.info("Initiating creation of a submission record");

        Submission submission = new Submission();
        submission.setSubmissionUrl(submissionDto.submission_url);
        submission.setSubmissionDate(new Date());
        submission.setSubmissionUpdated(new Date());
        submission.setAssignmentId(assignmentId);
        Submission createdSubmission = submissionRepository.save(submission);
        log.info("Successfully created submission record");
        return createdSubmission;
    }

    public List<Submission> getSubmissionsBy(String assignmentId) {
        if(assignmentRepository.findById(assignmentId).isPresent()) {
            List<Submission> submissions = submissionRepository.findByAssignmentId(assignmentId);
            return submissions;
        }
        return null;
    }
}
