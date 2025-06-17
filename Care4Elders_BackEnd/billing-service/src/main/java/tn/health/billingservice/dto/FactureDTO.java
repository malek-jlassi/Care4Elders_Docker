package tn.health.billingservice.dto;

import java.time.LocalDateTime;

public record FactureDTO(String id, String careRequestId, String patientId, String description,
                         double amount, LocalDateTime dateIssued, boolean paid) {}

