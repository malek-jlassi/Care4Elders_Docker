package tn.health.telemedservice.DTO;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import tn.health.telemedservice.entities.TeleconsultationStatus;


import java.time.LocalDateTime;

@Data
public class TeleconsultationDTO {

    private String id;
    private LocalDateTime consultationDate;
    private String status;
    private String patientId;
    private String doctorId;
    private String videoLink;
    private String notes;


    private int durationMinutes;

}
