package tn.health.careservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryResponseDTO {
    private String id;
    private String nomProduit;
    private String descriptionProduit;
    private String region;
    private double prix;
    private int qt;
    private String image;
    private String status;
    private double distanceInKm;
    private double deliveryCost;
    private double totalPrice;
    private LocalDateTime createdAt;
    private String userId;
    private String orderStatus;
} 