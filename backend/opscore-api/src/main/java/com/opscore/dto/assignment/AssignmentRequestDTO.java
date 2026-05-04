package com.opscore.dto.assignment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentRequestDTO {

    @NotBlank(message = "assignedTo is required")
    @Size(min = 3, max = 50)
    private String assignedTo;

    // getters/setters
}
