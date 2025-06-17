package tn.health.careservice.mappers;
//
//import org.springframework.stereotype.Component;
//import tn.health.careservice.dto.CareServiceRequestDTO;
//import tn.health.careservice.dto.UserDTO;
//import tn.health.careservice.entities.CareServiceRequest;
//
//@Component
//public class CareServiceRequestMapper {
//
//    public CareServiceRequest toEntity(CareServiceRequestDTO dto) {
//        CareServiceRequest entity = new CareServiceRequest();
//
//        entity.setId(dto.id());
//
//        if (dto.patient() != null) {
//            UserDTO patient = dto.patient();
//            entity.setPatient(new UserDTO(
//                    patient.id(),
//                    patient.role(),
//                    patient.firstName(),
//                    patient.lastName(),
//                    patient.age(),
//                    patient.email(),
//                    patient.phoneNumber(),
//                    patient.address()
//            ));
//        }
//
//        entity.setMaladiesChroniques(dto.maladiesChroniques());
//        entity.setCareType(dto.careType());
//        entity.setDescription(dto.description());
//        entity.setStartDate(dto.startDate());
//        entity.setEndDate(dto.endDate());
//        entity.setStatus(dto.status());
//        entity.setDateCreation(dto.dateCreation());
//        entity.setDateUpdate(dto.dateUpdate());
//
//        return entity;
//    }
//
//    public CareServiceRequestDTO toDTO(CareServiceRequest entity) {
//        UserDTO patientDTO = null;
//
//        if (entity.getPatient() != null) {
//            UserDTO p = entity.getPatient();
//            patientDTO = new UserDTO(
//                    p.getId(),
//                    p.getRole(),
//                    p.getFirstName(),
//                    p.getLastName(),
//                    p.getAge(),
//                    p.getEmail(),
//                    p.getPhoneNumber(),
//                    p.getAddress()
//            );
//        }
//
//        return new CareServiceRequestDTO(
//                entity.getId(),
//                patientDTO,
//                entity.getMaladiesChroniques(),
//                entity.getCareType(),
//                entity.getDescription(),
//                entity.getStartDate(),
//                entity.getEndDate(),
//                entity.getStatus(),
//                entity.getDateCreation(),
//                entity.getDateUpdate()
//        );
//    }
//}


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import tn.health.careservice.dto.CareServiceRequestDTO;
import tn.health.careservice.dto.UserDTO;
import tn.health.careservice.entities.CareServiceRequest;

@Mapper(componentModel = "spring")
public interface CareServiceRequestMapper {

    @Mappings({
            @Mapping(source = "patient.id", target = "patient") // Map user.id → patient
    })
    CareServiceRequest mapToEntity(CareServiceRequestDTO dto);

    @Mappings({
            @Mapping(target = "patient", ignore = true) // We'll set the full UserDTO manually if needed
    })
    CareServiceRequestDTO mapToDTO(CareServiceRequest entity);

    // Helper method for MapStruct (optional, not used directly in this case)
    default String map(UserDTO value) {
        return value.id();
    }
}
