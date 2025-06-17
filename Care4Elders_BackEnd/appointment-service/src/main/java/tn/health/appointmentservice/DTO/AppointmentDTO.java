package tn.health.appointmentservice.DTO;

import lombok.Data;
import tn.health.appointmentservice.entities.AppointmentStatus;
import tn.health.appointmentservice.entities.AppointmentType;

import java.time.LocalDateTime;

@Data
public class AppointmentDTO {


    private String patientId;
    private String doctorId;
    private LocalDateTime date;
    private int durationMinutes;
    private AppointmentType type;
    private AppointmentStatus status;
    private String reason;
    private String notes;
    private String prescriptionId;
}
