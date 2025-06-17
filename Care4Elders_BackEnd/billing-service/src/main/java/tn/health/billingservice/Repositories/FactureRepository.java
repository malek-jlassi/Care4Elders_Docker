package tn.health.billingservice.Repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tn.health.billingservice.Entities.Facture;

import java.util.List;

@Repository
public interface FactureRepository extends MongoRepository<Facture, String> {
    List<Facture> findByPatientId(String patientId);
}
