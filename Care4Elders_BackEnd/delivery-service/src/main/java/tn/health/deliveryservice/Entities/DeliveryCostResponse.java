package tn.health.deliveryservice.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryCostResponse {
    private double deliveryCost;
    private double productPrice;
    private double totalCost;
} 