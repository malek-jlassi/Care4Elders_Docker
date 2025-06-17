package tn.health.careservice.dto;

import tn.health.careservice.entities.CareStatus;
import tn.health.careservice.entities.CareType;
import tn.health.careservice.entities.ChronicDisease;

import java.time.LocalDateTime;
import java.util.List;



public record CareServiceRequestDTO(String id, UserDTO patient, List<ChronicDisease> maladiesChroniques,
                                    CareType careType, String description, LocalDateTime startDate,
                                    LocalDateTime endDate, CareStatus status, LocalDateTime dateCreation,
                                    LocalDateTime dateUpdate) {
    // Automatically generated getter methods for fields
}
