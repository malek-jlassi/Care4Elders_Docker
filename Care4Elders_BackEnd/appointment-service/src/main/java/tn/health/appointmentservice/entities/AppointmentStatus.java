package tn.health.appointmentservice.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum AppointmentStatus {
    @JsonProperty("pending")
    PENDING,
    @JsonProperty("confirmed")
    CONFIRMED,
    @JsonProperty("cancelled")
    CANCELLED,
    @JsonProperty("completed")
    COMPLETED
}
