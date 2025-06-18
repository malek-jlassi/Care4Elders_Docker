package tn.health.telemedservice.services;

import lombok.AllArgsConstructor;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import tn.health.telemedservice.Clients.UserClient;
import tn.health.telemedservice.DTO.TeleconsultationDTO;
import tn.health.telemedservice.DTO.TeleconsultationRequestDTO;
import tn.health.telemedservice.DTO.UserDTO;
import tn.health.telemedservice.entities.Teleconsultation;
import tn.health.telemedservice.entities.TeleconsultationStatus;
import tn.health.telemedservice.mappers.TeleconsultationMapper;
import tn.health.telemedservice.repositories.TeleconsultationRepository;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class TeleconsultationServiceImpl implements ITeleconsultationService {

    @Override
    public List<TeleconsultationDTO> getTeleconsultationsByUser(String userId) {
        List<Teleconsultation> teleconsultations = teleconsultationRepository.findByPatientId(userId);
        return teleconsultationMapper.mapToDtoList(teleconsultations);
    }

    @Autowired
    private  TeleconsultationRepository teleconsultationRepository;
   @Autowired
    private  TeleconsultationMapper teleconsultationMapper;
   @Autowired
   private WherebyService wherebyService;
   @Autowired
   private GoogleCalendarService googleCalendarService;
   @Autowired
   private EmailService emailService;
   @Autowired
   private UserClient userClient;

//    @Override
//    public TeleconsultationDTO add(TeleconsultationDTO teleconsultationDTO) {
//        Teleconsultation teleconsultation = teleconsultationMapper.mapToEntity(teleconsultationDTO);
//
//        // Définir un statut par défaut si non fourni
//        if (teleconsultation.getStatus() == null) {
//            teleconsultation.setStatus(TeleconsultationStatus.EN_ATTENTE);
//        }
//
//        teleconsultation.setCreatedAt(LocalDateTime.now());
//        return teleconsultationMapper.mapToDto(teleconsultationRepository.save(teleconsultation));
//    }


    @Override
    public TeleconsultationDTO add(TeleconsultationDTO dto) {
        // Création du lien Whereby (ou autre logique)
        String wherebyLink = wherebyService.createWherebyMeeting(dto.getConsultationDate(), 30);

        // 🔹 1. Récupérer l'utilisateur (patient)
        UserDTO patient = userClient.getUserById(dto.getPatientId());

        if (patient == null || patient.getEmail() == null) {
            throw new RuntimeException("Impossible de récupérer l'email du patient.");
        }

        try {
            // 🔹 2. Créer l'événement Google Calendar
            googleCalendarService.createGoogleCalendarEvent(wherebyLink,dto.getConsultationDate(), dto.getDurationMinutes());
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'insertion du google calendar.");
        }

        // 🔹 3. Sauvegarder la téléconsultation
        Teleconsultation entity = teleconsultationMapper.mapToEntity(dto);
        entity.setVideoLink(wherebyLink);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        Teleconsultation saved = teleconsultationRepository.save(entity);

        // 🔹 4. Envoyer email (exemple simplifié)
        String emailBody = "Votre téléconsultation est prévue le " + dto.getConsultationDate() +
                ". Voici le lien : " + wherebyLink;
        emailService.sendTeleconsultationEmail(patient.getEmail(), "Téléconsultation Confirmée", emailBody);

        return teleconsultationMapper.mapToDto(saved);
    }

    @Override
    public TeleconsultationDTO add(TeleconsultationRequestDTO requestDTO) {
        // Convertir RequestDTO → DTO
        TeleconsultationDTO dto = new TeleconsultationDTO();
        dto.setConsultationDate(requestDTO.getConsultationDate());
        dto.setDoctorId(requestDTO.getDoctorId());
        dto.setPatientId(requestDTO.getPatientId());
        dto.setStatus(requestDTO.getStatus());
        dto.setNotes(requestDTO.getNotes());
        dto.setDurationMinutes(requestDTO.getDurationMinutes());

        return this.add(dto);  // Appel de la méthode métier complète
    }


    @Override
    public TeleconsultationDTO update(String idTeleconsultation, Map<Object, Object> fields) {
        Teleconsultation teleconsultation = teleconsultationRepository.findById(idTeleconsultation)
                .orElseThrow(() -> new IllegalArgumentException("Teleconsultation not found: " + idTeleconsultation));

        fields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Teleconsultation.class, key.toString());
            if (field != null) {
                //field.setAccessible(true);
                Object convertedValue = value;

                // Gestion des types
                if (field.getType().equals(LocalDate.class)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    convertedValue = LocalDate.parse(value.toString(), formatter);
                } else if (field.getType().equals(LocalDateTime.class)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd['T'HH[:mm[:ss]]]");
                    convertedValue = LocalDateTime.parse(value.toString(), formatter);
                } else if (field.getType().isEnum()) {
                    convertedValue = Enum.valueOf((Class<Enum>) field.getType(), value.toString());
                }

                ReflectionUtils.setField(field, teleconsultation, convertedValue);
            }
        });

        return teleconsultationMapper.mapToDto(teleconsultationRepository.save(teleconsultation));
    }

    @Override
    public boolean delete(String idTeleconsultation) {
        teleconsultationRepository.deleteById(idTeleconsultation);
        return !teleconsultationRepository.existsById(idTeleconsultation);
    }

    @Override
    public Page<TeleconsultationDTO> getTeleconsultations(int pageNbr, int pageSize) {
        return teleconsultationRepository.findAll(PageRequest.of(pageNbr, pageSize))
                .map(teleconsultationMapper::mapToDto);
    }

    @Override
    public TeleconsultationDTO getTeleconsultation(String id) {
        return teleconsultationRepository.findById(id)
                .map(teleconsultationMapper::mapToDto)
                .orElseThrow(() -> new IllegalArgumentException("Teleconsultation not found"));
    }

    public List<TeleconsultationDTO> getPostConsultationReport() {
        List<Teleconsultation> consultations = teleconsultationRepository.findByStatus(TeleconsultationStatus.TERMINEE);

        return consultations.stream()
                .map(teleconsultationMapper::mapToDto)
                .collect(Collectors.toList());
    }




}
