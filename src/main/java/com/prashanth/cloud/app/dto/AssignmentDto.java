package com.prashanth.cloud.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@JsonIgnoreProperties(ignoreUnknown=false)
public class AssignmentDto {
    @NotEmpty(message = "Name cannot be null or empty")
    public String name;
    @Min(value = 1, message = "Number of attempts should be greater than or equal to 1")
    @Max(value = 100, message = "Number of attempts cannot be greater than 100")
    @NotNull(message = "Number of attempts cannot be null")
    public Integer num_of_attempts;
    @Min(value = 1, message = "Points should be greater than or equal to 1")
    @Max(value = 100, message = "Points cannot be greater than 100")
    @NotNull(message = "Points cannot be null")
    public Integer points;
    @NotNull(message = "Deadline cannot be null")
    public Date deadline;
}
