package tn.health.deliveryservice.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistanceDetails {
    private double distanceInKm;
    private double deliveryCost;
    private Location userLocation;
    private Location destinationLocation;
} 