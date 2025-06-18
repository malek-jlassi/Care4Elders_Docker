package tn.health.telemedservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tn.health.telemedservice.DTO.TeleconsultationDTO;
import tn.health.telemedservice.entities.Teleconsultation;

/**
 * Mapper interface for converting between {@link Teleconsultation} entities and {@link TeleconsultationDTO} DTOs.
 * <p>
 * This interface uses the <strong>MapStruct</strong> library to automatically generate
 * mapping implementations at compile time. The {@code @Mapper} annotation indicates that
 * this interface is a MapStruct mapper.
 * <p>
 * The {@code componentModel = "spring"} attribute specifies that the generated mapper
 * implementation should be a Spring bean, allowing it to be injected and managed by the
 * Spring framework.
 * <p>
 * MapStruct eliminates the need for manual mapping code by generating efficient and
 * type-safe mapping implementations.
 *
 * @see <a href="https://mapstruct.org/">MapStruct Documentation</a>
 */
@Mapper(componentModel = "spring")
public interface TeleconsultationMapper {

    /**
     * Converts a {@link TeleconsultationDTO} object to a {@link Teleconsultation} entity.
     *
     * @param dto the {@link TeleconsultationDTO} object to be mapped
     * @return the corresponding {@link Teleconsultation} entity
     */
    @Mapping(target = "status", source = "status")
    Teleconsultation mapToEntity(TeleconsultationDTO dto);

    /**
     * Converts a {@link Teleconsultation} entity to a {@link TeleconsultationDTO} object.
     *
     * @param entity the {@link Teleconsultation} entity to be mapped
     * @return the corresponding {@link TeleconsultationDTO} object
     */
    @Mapping(target = "id", source = "id")
    TeleconsultationDTO mapToDto(Teleconsultation entity);


    default java.util.List<TeleconsultationDTO> mapToDtoList(java.util.List<Teleconsultation> entities) {
        return entities.stream()
                .map(this::mapToDto)
                .collect(java.util.stream.Collectors.toList());
    }
}
