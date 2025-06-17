package tn.health.careservice.controllers;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.health.careservice.dto.CareRequestWithFactureResponseDTO;
import tn.health.careservice.dto.CareServiceRequestDTO;
import tn.health.careservice.entities.CareServiceRequest;
import tn.health.careservice.services.DoctorSuggestionService;
import tn.health.careservice.services.ICareServiceRequestService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/care")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"}, allowCredentials = "true")
@AllArgsConstructor
public class CareServiceRequestController {

    private ICareServiceRequestService careServiceRequestService;
    private DoctorSuggestionService doctorSuggestionService;

    @PostMapping
    public ResponseEntity<CareRequestWithFactureResponseDTO> create(@RequestBody CareServiceRequestDTO careServiceRequestdto) {
        return ResponseEntity.ok(careServiceRequestService.create(careServiceRequestdto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CareServiceRequest> update(@PathVariable String id, @RequestBody CareServiceRequestDTO dto) {
        return ResponseEntity.ok(careServiceRequestService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        careServiceRequestService.delete(id);
        return ResponseEntity.ok("Care Service request deleted successfully.");
    }

    @GetMapping
    public ResponseEntity<List<CareServiceRequest>> getAll() {
        return ResponseEntity.ok(careServiceRequestService.getAll());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<CareServiceRequest>> getByPatient(@PathVariable String patientId) {
        return ResponseEntity.ok(careServiceRequestService.getByPatient(patientId));
    }

    @PostMapping("/suggest-specialty")
    public ResponseEntity<String> suggestSpecialty(@RequestBody Map<String, String> body) {
        String description = body.get("description");
        String specialty = doctorSuggestionService.suggestDoctorSpecialty(description);
        return ResponseEntity.ok(specialty);
    }
}
