package tn.health.telemedservice.entities;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Document(collection = "teleconsultation")
public class Teleconsultation implements Serializable {

    @Id
    private String id;

    private LocalDateTime consultationDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private TeleconsultationStatus status;
    private String patientId;
    private String doctorId;
    private String videoLink;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
