package tn.health.careservice.services;



import tn.health.careservice.dto.CareRequestWithFactureResponseDTO;
import tn.health.careservice.dto.CareServiceRequestDTO;
import tn.health.careservice.entities.CareServiceRequest;

import java.util.List;

public interface ICareServiceRequestService {
    CareRequestWithFactureResponseDTO create(CareServiceRequestDTO dto );
    CareServiceRequest update(String id, CareServiceRequestDTO dto);
    void delete(String id);
    List<CareServiceRequest> getAll();
    List<CareServiceRequest> getByPatient(String patientId);
}
