package tn.esprit.spring.dto;

import lombok.Data;
import tn.esprit.spring.entity.OrderItem;
import tn.esprit.spring.entity.User;
import java.util.List;

@Data
public class DeliveryBillRequest {
    private User user;
    private String region;
    private double distanceInKm;
    private List<OrderItem> orderItems;
} 