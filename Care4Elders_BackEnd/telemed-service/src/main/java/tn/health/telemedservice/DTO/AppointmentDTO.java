package tn.health.telemedservice.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDTO {


    private String patientId;
    private String doctorId;
    private LocalDateTime date;
    private int durationMinutes;
    private String type;
    private String status;
    private String reason;
    private String notes;
    private String prescriptionId;
}
