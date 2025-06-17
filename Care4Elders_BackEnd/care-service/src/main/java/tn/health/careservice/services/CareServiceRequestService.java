package tn.health.careservice.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tn.health.careservice.dto.*;
import tn.health.careservice.entities.CareServiceRequest;
import tn.health.careservice.entities.CareStatus;
import tn.health.careservice.exeptions.ResourceNotFoundException;
import tn.health.careservice.feignclient.BillingClient;
import tn.health.careservice.feignclient.DeliveryClient;
import tn.health.careservice.feignclient.UserClient;
import tn.health.careservice.mappers.CareServiceRequestMapper;
import tn.health.careservice.repositories.CareServiceRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CareServiceRequestService implements ICareServiceRequestService {

    private CareServiceRequestRepository careServiceRequestRepository;
    private CareServiceRequestMapper mapper;
    @Qualifier("tn.health.careservice.feignclient.UserClient")
    private UserClient userClient;
    private BillingClient billingClient;
    private DeliveryClient deliveryClient;

    @Override
    public CareRequestWithFactureResponseDTO create(CareServiceRequestDTO dto) {
        try {
            log.info("Creating new care service request: {}", dto);
            
            // 1. Validate DTO
            if (dto == null) {
                throw new IllegalArgumentException("Care request DTO cannot be null");
            }
            if (dto.patient() == null || dto.patient().id() == null) {
                throw new IllegalArgumentException("Patient information is required");
            }

            // 2. Convert DTO to entity
            CareServiceRequest request = new CareServiceRequest();
            request.setPatient(dto.patient().id());
            request.setCareType(dto.careType());
            request.setDescription(dto.description());
            request.setMaladiesChroniques(dto.maladiesChroniques());
            request.setStartDate(dto.startDate());
            request.setEndDate(dto.endDate());
            request.setStatus(CareStatus.EN_ATTENTE);
            request.setDateCreation(LocalDateTime.now());
            request.setDateUpdate(LocalDateTime.now());

            log.debug("Converted request entity: {}", request);

            // 3. Save entity
            CareServiceRequest saved = careServiceRequestRepository.save(request);
            log.info("Saved care request with ID: {}", saved.getId());

            UserDTO userDTO = null;
            FactureResponseDTO factureResponse = null;
            try {
                // 4. Fetch full user data using Feign client
                userDTO = userClient.getUserById(saved.getPatient());
                saved.setPatientInfo(userDTO);
                log.debug("Retrieved user info: {}", userDTO);
            } catch (Exception e) {
                log.error("Error retrieving user info: {}", e.getMessage());
            }

            try {
                // 5. Create billing record and get facture
                CareRequestPayload payload = new CareRequestPayload(
                        saved.getId(),
                        saved.getPatient(),
                        saved.getCareType().name(),
                        saved.getDescription()
                );
                factureResponse = billingClient.createBillFromRequest(payload);
                log.info("Created billing record and received facture for care request ID: {}", saved.getId());
            } catch (Exception e) {
                log.error("Error creating billing record: {}", e.getMessage());
            }

            // Map saved CareServiceRequest to DTO for response
            CareServiceRequestDTO responseDTO = mapper.mapToDTO(saved);
            return new CareRequestWithFactureResponseDTO(responseDTO, factureResponse);
        } catch (Exception e) {
            log.error("Error creating care request: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create care request: " + e.getMessage(), e);
        }
    }

    @Override
    public CareServiceRequest update(String id, CareServiceRequestDTO dto) {
        try {
            CareServiceRequest existing = careServiceRequestRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + id));
            
            log.info("Updating care request with ID: {}", id);
            log.debug("Update DTO: {}", dto);

            CareServiceRequest updated = mapper.mapToEntity(dto);
            updated.setId(existing.getId());
            updated.setDateCreation(existing.getDateCreation());
            updated.setDateUpdate(LocalDateTime.now());
            
            return careServiceRequestRepository.save(updated);
        } catch (Exception e) {
            log.error("Error updating care request: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update care request: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            careServiceRequestRepository.deleteById(id);
            log.info("Deleted care request with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting care request: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete care request: " + e.getMessage(), e);
        }
    }

    @Override
    public List<CareServiceRequest> getAll() {
        try {
            return careServiceRequestRepository.findAll();
        } catch (Exception e) {
            log.error("Error retrieving all care requests: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve care requests: " + e.getMessage(), e);
        }
    }

    @Override
    public List<CareServiceRequest> getByPatient(String patientId) {
        try {
            return careServiceRequestRepository.findByPatient(patientId);
        } catch (Exception e) {
            log.error("Error retrieving care requests for patient {}: {}", patientId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve care requests for patient: " + e.getMessage(), e);
        }
    }
}