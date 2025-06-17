package tn.health.appointmentservice.mappers;

import org.mapstruct.Mapper;
import tn.health.appointmentservice.DTO.AppointmentDTO;
import tn.health.appointmentservice.entities.Appointment;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {


    Appointment mapToEntity(AppointmentDTO dto);

    AppointmentDTO mapToDto(Appointment entity);
}
