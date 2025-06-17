package tn.health.telemedservice.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeleconsultationReportDTO {
    private String id;
    private String doctorId;
    private String doctorName;
    private String patientId;
    private String patientName;
    private LocalDateTime date;
    private String status;
    private String videoLink;

    // Getters & Setters
}

