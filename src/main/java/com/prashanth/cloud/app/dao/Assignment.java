package com.prashanth.cloud.app.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Setter;

import java.util.Date;

@Setter
@Entity
@Table(name="assignment")
public class Assignment {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    public String id;
    @Column(nullable = false)
    public String name;
    @Column(name="number_of_attempts", nullable = false)
    @JsonProperty("num_of_attempts")
    public Integer numOfAttempts;
    @Column(name="points", nullable = false)
    public Integer points;
    @Column(nullable = false)
    public Date deadline;
    @Column(name="assignment_created", nullable = false)
    @JsonProperty("assignment_created")
    public Date assignmentCreated;
    @Column(name="assignment_updated", nullable = false)
    @JsonProperty("assignment_updated")
    public Date assignmentUpdated;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "assignment_user")
    public User user;
}
