package tn.health.deliveryservice.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "delivery_bills")
public class DeliveryBill {
    @Id
    private String id;
    private User user;
    private String region;
    private double distanceInKm;
    private List<OrderItem> orderItems;
    private double totalAmount;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private String paymentId;
}