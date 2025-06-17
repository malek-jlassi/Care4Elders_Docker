package tn.health.deliveryservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tn.health.deliveryservice.Entities.DeliveryBill;
import tn.health.deliveryservice.Entities.DeliveryStatus;
import java.util.List;

public interface DeliveryBillRepository extends MongoRepository<DeliveryBill, String> {
    List<DeliveryBill> findByUserId(String userId);
    List<DeliveryBill> findByOrderStatus(DeliveryStatus status);
    List<DeliveryBill> findByRegion(String region);
} 