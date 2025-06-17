package tn.health.deliveryservice.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "delivery_bills")
public class DeliveryBill {
    @Id
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
    private Location userLocation;
    private Location destinationLocation;
    private LocalDateTime createdAt;
    private String userId;
    private DeliveryStatus orderStatus;
} 