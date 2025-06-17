package tn.health.careservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryRequestPayload {
    private String productId;
    private String nomProduit;
    private String descriptionProduit;
    private String region;
    private double prix;
    private int qt;
    private String image;
    private String status;
} 