package com.opscore.dto.incident;

import com.opscore.enums.Category;
import com.opscore.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class IncidentRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 5,max = 100, message = "Title must be less than 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10,max = 500, message = "Description must be less than 500 characters")
    private String description;

    @NotNull(message = "Priority is required")
    private Priority priority;

    @NotNull(message = "Category is required")
    private Category category;
}

