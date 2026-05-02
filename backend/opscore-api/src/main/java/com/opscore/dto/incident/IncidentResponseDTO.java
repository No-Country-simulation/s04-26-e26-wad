package com.opscore.dto.incident;

import com.opscore.enums.Category;
import com.opscore.enums.IncidentStatus;
import com.opscore.enums.Priority;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IncidentResponseDTO {

    private Long id;
    private String title;
    private String description;
    private IncidentStatus status;
    private Priority priority;
    private Category category;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}

