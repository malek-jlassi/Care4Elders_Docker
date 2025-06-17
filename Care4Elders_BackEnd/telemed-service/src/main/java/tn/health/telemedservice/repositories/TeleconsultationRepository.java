package tn.health.telemedservice.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tn.health.telemedservice.entities.Teleconsultation;
import tn.health.telemedservice.entities.TeleconsultationStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeleconsultationRepository  extends MongoRepository<Teleconsultation, String> {

    Optional<Teleconsultation> findByDoctorId(String doctorId);


    List<Teleconsultation> findByStatus(TeleconsultationStatus teleconsultationStatus);
}
