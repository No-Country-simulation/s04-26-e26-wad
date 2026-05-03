package com.opscore.dto.incident;

import com.opscore.enums.Category;
import com.opscore.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class IncidentRequestDTO {

    @NotBlank
    private String title;

    @Size(max = 1000)
    private String description;

    @NotNull
    private Priority priority;

    @NotNull
    private Category category;
}

