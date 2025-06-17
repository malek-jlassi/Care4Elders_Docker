package tn.health.billingservice.dto;

public record CareRequestPayload(
        String id,
        String patientId,
        String careType,
        String description
) {}

