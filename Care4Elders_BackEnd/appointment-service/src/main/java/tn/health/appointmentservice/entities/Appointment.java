package tn.health.appointmentservice.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Document(collection = "appointment")
public class Appointment {
    @Id
    private String id;

    private String patientId;
    private String doctorId;

    private LocalDateTime date;
    private int durationMinutes;

    private AppointmentType type;   // "in_person" or "teleconsultation"
    private AppointmentStatus status; // "pending", "confirmed", "cancelled", "completed"

    private String reason;
    private String notes;

    private String prescriptionId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
