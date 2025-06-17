package tn.health.telemedservice.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.health.telemedservice.DTO.TeleconsultationDTO;
import tn.health.telemedservice.DTO.TeleconsultationReportDTO;
import tn.health.telemedservice.DTO.TeleconsultationRequestDTO;
import tn.health.telemedservice.entities.Teleconsultation;
import tn.health.telemedservice.entities.TeleconsultationStatus;
import tn.health.telemedservice.repositories.TeleconsultationRepository;
import tn.health.telemedservice.services.ITeleconsultationService;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teleconsultations")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class TeleconsultationController {

    @Autowired
    private ITeleconsultationService teleconsultationService;
    private TeleconsultationRepository teleconsultationRepository;

    @PostMapping
    public TeleconsultationDTO add(@RequestBody TeleconsultationRequestDTO requestDTO) {
        return teleconsultationService.add(requestDTO);
    }

    @PatchMapping("/{id}")
    public TeleconsultationDTO patchUpdate(@RequestBody Map<Object, Object> fields, @PathVariable String id) {
        return teleconsultationService.update(id, fields);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable String id) {
        return teleconsultationService.delete(id);
    }

    @GetMapping
    public Page<TeleconsultationDTO> getTeleconsultations(@RequestParam int pageNbr, @RequestParam int pageSize) {
        return teleconsultationService.getTeleconsultations(pageNbr, pageSize);
    }

    @GetMapping("/{id}")
    public TeleconsultationDTO getTeleconsultation(@PathVariable String id) {
        return teleconsultationService.getTeleconsultation(id);
    }

    @GetMapping("/report/post-consultation")
    public ResponseEntity<List<TeleconsultationReportDTO>> getPostConsultationReport() {
        List<Teleconsultation> consultations = teleconsultationRepository.findByStatus(TeleconsultationStatus.TERMINEE);

        List<TeleconsultationReportDTO> reportData = consultations.stream().map(consult -> {
            TeleconsultationReportDTO dto = new TeleconsultationReportDTO();
            dto.setId(consult.getId());
            dto.setDoctorId(consult.getDoctorId());
            dto.setPatientId(consult.getPatientId());
            dto.setDate(consult.getConsultationDate());
            dto.setStatus(consult.getStatus().name());
            dto.setVideoLink(consult.getVideoLink());

            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(reportData);
    }

}
