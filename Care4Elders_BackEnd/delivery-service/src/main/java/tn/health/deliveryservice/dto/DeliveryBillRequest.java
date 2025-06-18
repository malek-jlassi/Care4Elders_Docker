package tn.health.deliveryservice.dto;

import lombok.Data;
import tn.health.deliveryservice.Entities.OrderItem;
import tn.health.deliveryservice.Entities.User;
import java.util.List;

@Data
public class DeliveryBillRequest {
    private User user;
    private String region;
    private double distanceInKm;
    private List<OrderItem> orderItems;
}
