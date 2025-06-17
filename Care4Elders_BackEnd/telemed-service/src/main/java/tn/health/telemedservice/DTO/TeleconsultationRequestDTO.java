package tn.health.telemedservice.DTO;


import lombok.Data;
import tn.health.telemedservice.entities.TeleconsultationStatus;

import java.time.LocalDateTime;

@Data
public class TeleconsultationRequestDTO {
    private LocalDateTime consultationDate;
    private String status; // optionnel
    private String patientId;
    private String doctorId;
    private Integer durationMinutes;// facultatif, défaut = 30 min
    private String notes;
}