package tn.health.careservice.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tn.health.careservice.entities.CareServiceRequest;

import java.util.List;
@Repository
public interface CareServiceRequestRepository extends MongoRepository<CareServiceRequest, String> {
    List<CareServiceRequest> findByPatient(String patientId);
}