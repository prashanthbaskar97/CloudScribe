package com.prashanth.cloud.app.service;

import com.hari.cloud.app.dao.Assignment;
import com.hari.cloud.app.dao.User;
import com.hari.cloud.app.dto.AssignmentDto;
import com.hari.cloud.app.repository.AssignmentRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AssignmentService {
    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private UserService userService;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Assignment> getAllAssignments() {
        return (List<Assignment>) assignmentRepository.findAll();
    }

    public Assignment createAssignment(AssignmentDto assignmentDto) throws PSQLException {
        log.info("Initiating creation of an assignment record");
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userService.getUserBy(email);
        Assignment assignment = new Assignment();
        assignment.setName(assignmentDto.name);
        assignment.setNumOfAttempts(assignmentDto.num_of_attempts);
        assignment.setPoints(assignmentDto.points);
        assignment.setDeadline(assignmentDto.deadline);
        assignment.setAssignmentCreated(new Date());
        assignment.setAssignmentUpdated(new Date());
        assignment.setUser(user);
        Assignment createdAssignment = assignmentRepository.save(assignment);
        log.info("Successfully created assignment record");
        return createdAssignment;
    }

    public Assignment getAssignmentBy(String id) {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        if(assignmentRepository.findById(id).isPresent()) {
            Assignment assignment = assignmentRepository.findById(id).get();
            // Only return records belonging to the current user
            if(assignment.user.getEmail().equals(email)) return assignment;
        }
        return null;
    }

    public Assignment updateAssignment(AssignmentDto updatedAssignment, String id) {
        log.info("Initiating update assignment record");
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        if(!assignmentRepository.findById(id).isPresent()) {
            log.info("Assignment with given id not found");
            return null;
        }
        Assignment assignment = assignmentRepository.findById(id).get();
        // Only update records belonging to the current user
        if(!assignment.user.getEmail().equals(email)) return null;

        assignment.setName(updatedAssignment.name);
        assignment.setNumOfAttempts(updatedAssignment.num_of_attempts);
        assignment.setPoints(updatedAssignment.points);
        assignment.setDeadline(updatedAssignment.deadline);
        assignment.setAssignmentUpdated(new Date());
        Assignment updatedAssignmentRecord = assignmentRepository.save(assignment);
        log.info("Assignment with given id successfully updated");
        return updatedAssignmentRecord;
    }

    @Transactional
    public Boolean deleteAssignmentBy(String id) {
        log.info("Initiating delete assignment record");
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        if(!assignmentRepository.findById(id).isPresent()) {
            log.info("Delete assignment failed as record not found");
            return false;
        }
        Assignment assignment = assignmentRepository.findById(id).get();
        // Only delete records belonging to the current user
        if(!assignment.user.getEmail().equals(email)) {
            log.info("Delete assignment failed as record not found for current user");
            return false;
        }
        entityManager.remove(assignment);
        entityManager.flush();
        log.info("Delete assignment successful");
        return true;
    }
}
