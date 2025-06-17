package tn.health.appointmentservice.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum AppointmentType {
    @JsonProperty("in_person")
    IN_PERSON,
    @JsonProperty("teleconsultation")
    TELECONSULTATION
}
