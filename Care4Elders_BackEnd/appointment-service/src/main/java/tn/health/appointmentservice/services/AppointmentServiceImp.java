package tn.health.appointmentservice.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.health.appointmentservice.entities.Appointment;
import tn.health.appointmentservice.entities.AppointmentType;
import tn.health.appointmentservice.exeptions.ResourceNotFoundException;
import tn.health.appointmentservice.repositories.AppointmentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AppointmentServiceImp implements IAppointmentService{

    private AppointmentRepository appointmentRepository;

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    public Optional<Appointment> getAppointmentById(String id) {
        return appointmentRepository.findById(id);
    }

    /*@Override
    public Appointment createAppointment(Appointment appointment) {
        appointment.setCreatedAt(LocalDateTime.now());
        return appointmentRepository.save(appointment);
    }*/

    @Override
    public Appointment createAppointment(Appointment appointment) {
        appointment.setCreatedAt(LocalDateTime.now());

        // Définir le type automatiquement (exemple de critère : présence d’un lien vidéo)
        if (appointment.getType() == null) {
            if (appointment.getNotes() != null && appointment.getNotes().toLowerCase().contains("video")) {
                appointment.setType(AppointmentType.TELECONSULTATION);
            } else {
                appointment.setType(AppointmentType.IN_PERSON);
            }
        }

        return appointmentRepository.save(appointment);
    }


    @Override
    public Appointment updateAppointment(String id, Appointment updatedData) {
        Optional<Appointment> existingOpt = appointmentRepository.findById(id);

        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Appointment not found with id: " + id);
        }

        Appointment existing = existingOpt.get();

        // Update only the modifiable fields
        existing.setPatientId(updatedData.getPatientId());
        existing.setDoctorId(updatedData.getDoctorId());
        existing.setDate(updatedData.getDate());
        existing.setDurationMinutes(updatedData.getDurationMinutes());
        existing.setType(updatedData.getType());
        existing.setStatus(updatedData.getStatus());
        existing.setReason(updatedData.getReason());
        existing.setNotes(updatedData.getNotes());
        existing.setPrescriptionId(updatedData.getPrescriptionId());

        existing.setUpdatedAt(LocalDateTime.now());

        return appointmentRepository.save(existing);
    }


    @Override
    public void deleteAppointment(String id) {
        appointmentRepository.deleteById(id);
    }
}
