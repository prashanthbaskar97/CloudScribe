package com.prashanth.cloud.app.controller;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.hari.cloud.app.dao.Assignment;
import com.hari.cloud.app.dao.Submission;
import com.hari.cloud.app.dto.AssignmentDto;
import com.hari.cloud.app.dto.SubmissionDto;
import com.hari.cloud.app.service.AssignmentService;
import com.hari.cloud.app.service.SubmissionService;
import com.hari.cloud.app.service.UserService;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.timgroup.statsd.NonBlockingStatsDClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.attribute.AclEntryType;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class AssignmentController {
    @Autowired
    AssignmentService assignmentService;

    @Autowired
    UserService userService;

    @Autowired
    SubmissionService submissionService;

    @Autowired
    NonBlockingStatsDClient statsd;

    @Autowired
    private Environment env;

    private AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();

    @Transactional(propagation= Propagation.REQUIRED, readOnly=true, noRollbackFor=Exception.class)
    @GetMapping("/v2/assignments")
    public ResponseEntity getAssignments() {
        long startTime = System.currentTimeMillis();
        log.info("Get assignments API invoked");
        statsd.incrementCounter("getassignments-invoke-count");
        List<Assignment> assignments = assignmentService.getAllAssignments();
        statsd.recordExecutionTime("execution-latency", System.currentTimeMillis()-startTime);
        if(assignments == null) {
            log.info("Responded to get assignment with forbidden");
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        } else {
            log.info("Responded to get assignment with success");
            return new ResponseEntity(assignments, HttpStatus.OK);
        }
    }

    @GetMapping("/v1/assignments/{id}")
    public ResponseEntity getAssignment(@PathVariable("id") String id) {
        long startTime = System.currentTimeMillis();
        log.info("Get assignment with id API invoked");
        statsd.incrementCounter("getAssignmentId-invoke-count");
        Assignment assignment = assignmentService.getAssignmentBy(id);
        statsd.recordExecutionTime("execution-latency", System.currentTimeMillis()-startTime);
        if(assignment == null) {
            log.info("Responded to get assignment with id with forbidden");
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        } else {
            log.info("Responded to get assignment with id with success");
            return new ResponseEntity(assignment, HttpStatus.OK);
        }
    }

    @PostMapping("/v2/assignments")
    public ResponseEntity createAssignment(@RequestBody @Valid AssignmentDto assignmentDto) throws PSQLException {
        long startTime = System.currentTimeMillis();
        log.info("Post assignment with API invoked");
        statsd.incrementCounter("createAssignment-invoke-count");
        Assignment assignment = assignmentService.createAssignment(assignmentDto);
        statsd.recordExecutionTime("execution-latency", System.currentTimeMillis()-startTime);
        if(assignment == null) {
            log.info("Responded to post assignment with forbidden");
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        } else {
            log.info("Responded to post assignment with created");
            return new ResponseEntity(assignment, HttpStatus.CREATED);
        }
    }

    @PostMapping("/v2/assignments/{id}/submission")
    public ResponseEntity submitAssignment(@PathVariable("id") String assignmentId, @RequestBody @Valid SubmissionDto submissionDto) throws PSQLException {
        long startTime = System.currentTimeMillis();
        statsd.incrementCounter("assignment-submission");
        Assignment assignment = assignmentService.getAssignmentBy(assignmentId);
        // Reject if assignment does not exist
        if(assignmentId.isEmpty() || assignment == null) {
            log.info("Responded to create submission with failure");
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
        // Reject assignment if past deadline
        if((new Date()).after(assignment.deadline)) {
            log.info("Responded to create submission with failure");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        List<Submission> prevSubmissions = submissionService.getSubmissionsBy(assignmentId);
        // Reject assignment if max submission limit reached
        if(prevSubmissions.size() >= assignment.numOfAttempts) {
            log.info("Responded to create submission with failure");
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
        try {
           new URL(submissionDto.submission_url);
        } catch (MalformedURLException exception) {
            log.info("Responded malformed url exception");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        // Insert submission record into database
        log.info("Create submission with assignment id invoked");
        Submission createdSubmission = submissionService.createSubmission(submissionDto, assignmentId);
        statsd.recordExecutionTime("execution-latency", System.currentTimeMillis()-startTime);
        if(createdSubmission != null) {
            try {
                publishToTopic(snsClient, createdSubmission, env.getProperty("sns.topic.arn"), String.valueOf(prevSubmissions.size()+1));
            } catch (Exception e) {
                log.info("Responded to create submission with failure "+e.getMessage());
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            log.info("Responded to create submission with success");
            return new ResponseEntity(createdSubmission, HttpStatus.CREATED);
        } else {
            log.info("Responded to create submission with failure");
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/v1/assignments/{id}")
    public ResponseEntity updateAssignment(@PathVariable("id") String id, @RequestBody @Valid AssignmentDto assignmentDto) {
        long startTime = System.currentTimeMillis();
        log.info("Put assignment with API invoked");
        statsd.incrementCounter("updateAssignment-invoke-count");
        Assignment assignment = assignmentService.updateAssignment(assignmentDto, id);
        statsd.recordExecutionTime("execution-latency", System.currentTimeMillis()-startTime);
        if(assignment == null) {
            log.info("Responded to put assignment with not found");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            log.info("Responded to put assignment with success");
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }

    @DeleteMapping("/v1/assignments/{id}")
    public ResponseEntity deleteAssignment(@PathVariable("id") String id) {
        long startTime = System.currentTimeMillis();
        statsd.incrementCounter("deleteAssignment-invoke-count");
        Boolean isSuccessful = assignmentService.deleteAssignmentBy(id);
        log.info("Delete assignment with API invoked");
        statsd.recordExecutionTime("execution-latency", System.currentTimeMillis()-startTime);
        if(isSuccessful) {
            log.info("Responded to delete assignment with id success");
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
            log.info("Responded to delete assignment with id not found");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    public void publishToTopic(AmazonSNS snsClient, Submission submission, String topicArn, String attemptNumber) throws PSQLException {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        var user = userService.getUserBy(email);
        var firstname = user.getFirst_name();
        var assignmentTitle = assignmentService.getAssignmentBy(submission.assignmentId).name;
        PublishRequest request = new PublishRequest(topicArn, submission.submissionUrl);

        request.addMessageAttributesEntry("url", new MessageAttributeValue().withDataType("String").withStringValue(submission.submissionUrl));
        request.addMessageAttributesEntry("attemptNumber", new MessageAttributeValue().withDataType("String").withStringValue(attemptNumber));
        request.addMessageAttributesEntry("email", new MessageAttributeValue().withDataType("String").withStringValue(email));
        request.addMessageAttributesEntry("assignmentId", new MessageAttributeValue().withDataType("String").withStringValue(submission.assignmentId));
        request.addMessageAttributesEntry("assignmentTitle", new MessageAttributeValue().withDataType("String").withStringValue(assignmentTitle));
        request.addMessageAttributesEntry("firstname", new MessageAttributeValue().withDataType("String").withStringValue(firstname));
        PublishResult result = snsClient.publish(request);
        System.out.println(result.getMessageId() + " Message sent. Status is " + result.getSdkHttpMetadata());
    }
}

