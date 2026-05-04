package com.opscore.dto.assignment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentRequestDTO {

    @NotBlank(message = "assignedTo is required")
    private String assignedTo;

    // getters/setters
}
