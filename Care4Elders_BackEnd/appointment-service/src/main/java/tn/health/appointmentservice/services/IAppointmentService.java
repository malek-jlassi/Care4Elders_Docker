package tn.health.appointmentservice.services;

import tn.health.appointmentservice.entities.Appointment;

import java.util.List;
import java.util.Optional;

public interface IAppointmentService {
    List<Appointment> getAllAppointments();
    Optional<Appointment> getAppointmentById(String id);
    Appointment createAppointment(Appointment appointment);
    Appointment updateAppointment(String id, Appointment updatedData) ;
    void deleteAppointment(String id);
}
