package com.opscore.dto.assignment;

import java.time.LocalDateTime;

public class AssignmentResponseDTO {

    private String assignedTo;
    private String assignedBy;
    private LocalDateTime assignedAt;

    public AssignmentResponseDTO(String assignedTo, String assignedBy, LocalDateTime assignedAt) {
        this.assignedTo = assignedTo;
        this.assignedBy = assignedBy;
        this.assignedAt = assignedAt;
    }

    public String getAssignedTo() { return assignedTo; }
    public String getAssignedBy() { return assignedBy; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
}

