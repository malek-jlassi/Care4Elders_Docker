package tn.health.appointmentservice.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tn.health.appointmentservice.entities.Appointment;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {
}
