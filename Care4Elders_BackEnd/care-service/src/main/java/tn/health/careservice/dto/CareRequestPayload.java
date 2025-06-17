package tn.health.careservice.dto;

public record CareRequestPayload(
        String id,
        String patientId,
        String careType,
        String description
) {}