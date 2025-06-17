package tn.esprit.spring.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tn.esprit.spring.entity.DeliveryBill;

public interface DeliveryBillRepository extends MongoRepository<DeliveryBill, String> {
} 