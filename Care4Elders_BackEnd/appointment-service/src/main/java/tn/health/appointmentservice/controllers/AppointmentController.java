package tn.health.appointmentservice.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.health.appointmentservice.entities.Appointment;
import tn.health.appointmentservice.services.IAppointmentService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/appointment")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class AppointmentController {
    private IAppointmentService appointmentService;


    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/{id}")
    public Optional<Appointment> getAppointmentById(@PathVariable String id) {
        return appointmentService.getAppointmentById(id);
    }

    @PostMapping
    public Appointment createAppointment(@RequestBody Appointment appointment) {
        return appointmentService.createAppointment(appointment);
    }

    @PutMapping("/{id}")
    public Appointment updateAppointment(@PathVariable String id, @RequestBody Appointment appointment) {
        return appointmentService.updateAppointment(id, appointment);
    }

    @DeleteMapping("/{id}")
    public void deleteAppointment(@PathVariable String id) {
        appointmentService.deleteAppointment(id);
    }

    @GetMapping("/user/{userId}")
    public List<Appointment> getAppointmentsByUser(@PathVariable String userId) {
        System.out.println("Fetching appointments for userId: " + userId);
        List<Appointment> result = appointmentService.getAppointmentsByUser(userId);
        System.out.println("Found appointments: " + (result != null ? result.size() : "null"));
        return result;
    }
}
