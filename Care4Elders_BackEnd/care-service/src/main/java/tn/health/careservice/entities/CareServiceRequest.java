package tn.health.careservice.entities;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import tn.health.careservice.dto.UserDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "care_service_request")
public class CareServiceRequest {
    @Id
    private String id;
    private String patient;
    private List<ChronicDisease> maladiesChroniques;
    private CareType careType;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private CareStatus status = CareStatus.EN_ATTENTE;
    private LocalDateTime dateCreation;
    private LocalDateTime dateUpdate;
    @Transient
    private UserDTO patientInfo;

    public void setPatientInfo(UserDTO patientInfo) {
        this.patientInfo = patientInfo;
    }

    public UserDTO getPatientInfo() {
        return patientInfo;
    }


}