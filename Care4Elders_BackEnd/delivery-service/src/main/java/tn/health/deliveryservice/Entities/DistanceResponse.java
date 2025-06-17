package tn.health.deliveryservice.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistanceResponse {
    private String fromRegion;
    private String toRegion;
    private double distance;
    private double estimatedCost;
} 