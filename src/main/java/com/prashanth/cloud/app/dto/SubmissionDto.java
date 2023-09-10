package com.prashanth.cloud.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Component;

@Component
@JsonIgnoreProperties(ignoreUnknown=false)
public class SubmissionDto {
    @NotEmpty(message = "Submission url cannot be null or empty")
    public String submission_url;
}
