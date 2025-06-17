package tn.health.careservice.dto;

public record CareRequestWithFactureResponseDTO(
    CareServiceRequestDTO careRequest,
    FactureResponseDTO facture
) {}
