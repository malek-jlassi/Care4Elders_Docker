package tn.health.telemedservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import tn.health.telemedservice.DTO.TeleconsultationDTO;
import tn.health.telemedservice.DTO.TeleconsultationRequestDTO;

import java.util.List;
import java.util.Map;


public interface ITeleconsultationService {


    TeleconsultationDTO add(TeleconsultationDTO dto);

    TeleconsultationDTO add(TeleconsultationRequestDTO requestDTO);

    TeleconsultationDTO update(String idTeleconsultation, Map<Object, Object> fields);

    boolean delete(String idTeleconsultation);

    Page<TeleconsultationDTO> getTeleconsultations(int pageNbr, int pageSize);

    TeleconsultationDTO getTeleconsultation(String id);

    //TeleconsultationDTO getByDoctorName(String doctorName);
    List<TeleconsultationDTO> getTeleconsultationsByUser(String userId);
}
