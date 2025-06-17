package tn.health.appointmentservice.clients;
import org.springframework.stereotype.Component;
import tn.health.appointmentservice.DTO.UserDTO;

import java.util.Collections;
import java.util.List;

@Component
public class UserClientFallback implements UserClient{


    @Override
    public List<UserDTO> getAllUsers() {
        return Collections.emptyList();
    }
    @Override
    public List<UserDTO> getUserByRole(String role) {
        System.err.println("User service unavailable. Returning empty list.");
        return Collections.emptyList();
    }



}
