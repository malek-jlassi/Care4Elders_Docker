package tn.health.deliveryservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tn.health.deliveryservice.Entities.DeliveryBill;
import tn.health.deliveryservice.Entities.DeliveryStatus;
import tn.health.deliveryservice.Entities.PaymentStatus;

import java.util.List;

public interface DeliveryBillRepository extends MongoRepository<DeliveryBill, String> {
    List<DeliveryBill> findByUserId(String userId);
    List<DeliveryBill> findByStatus(PaymentStatus status);
    List<DeliveryBill> findByRegion(String region);
} 