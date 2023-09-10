package com.prashanth.cloud.app.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Setter;

import java.util.Date;

@Setter
@Entity
@Table(name="submission")
public class Submission {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    public String id;
    @Column(name = "submission_url", nullable = false)
    @JsonProperty("submission_url")
    public String submissionUrl;
    @Column(name = "assignment_id", nullable = false)
    @JsonProperty("assignment_id")
    public String assignmentId;
    @Column(name = "submission_date", nullable = false)
    @JsonProperty("submission_date")
    public Date submissionDate;
    @Column(name = "submission_updated", nullable = false)
    @JsonProperty("submission_updated")
    public Date submissionUpdated;
}
