package com.opscore.service.impl;

import com.opscore.dto.incident.IncidentRequestDTO;
import com.opscore.dto.incident.IncidentResponseDTO;
import com.opscore.entity.Incident;
import com.opscore.enums.IncidentStatus;
import com.opscore.repository.IncidentRepository;
import com.opscore.service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;

    @Override
    public IncidentResponseDTO createIncident(IncidentRequestDTO request) {

        // 🔥 Regla de negocio: valores iniciales
        Incident incident = Incident.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .category(request.getCategory())
                .status(IncidentStatus.OPEN)
                .resolvedAt(null)
                .build();

        Incident saved = incidentRepository.save(incident);

        return mapToResponse(saved);
    }

    // Mapper manual (simple y claro)
    private IncidentResponseDTO mapToResponse(Incident incident) {
        IncidentResponseDTO dto = new IncidentResponseDTO();

        dto.setId(incident.getId());
        dto.setTitle(incident.getTitle());
        dto.setDescription(incident.getDescription());
        dto.setStatus(incident.getStatus());
        dto.setPriority(incident.getPriority());
        dto.setCategory(incident.getCategory());
        dto.setCreatedAt(incident.getCreatedAt());
        dto.setResolvedAt(incident.getResolvedAt());

        return dto;
    }

    @Override
    public List<IncidentResponseDTO> getAllIncidents() {

        return incidentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public IncidentResponseDTO getIncidentById(Long id) {

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found with id: " + id));

        return mapToResponse(incident);
    }

}

