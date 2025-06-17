package tn.health.careservice.dto;


import java.time.LocalDateTime;

public record FactureResponseDTO(
        String id,
        String careRequestId,
        String patientId,
        String description,
        double amount,
        LocalDateTime dateIssued,
        boolean paid
) {}
