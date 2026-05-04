package com.opscore.controller;

import com.opscore.dto.assignment.AssignmentRequestDTO;
import com.opscore.service.AssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/incidents")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<Void> assignIncident(
            @PathVariable Long id,
            @Valid @RequestBody AssignmentRequestDTO request
    ) {

        assignmentService.assignIncident(id, request);

        return ResponseEntity.ok().build();
    }
}
