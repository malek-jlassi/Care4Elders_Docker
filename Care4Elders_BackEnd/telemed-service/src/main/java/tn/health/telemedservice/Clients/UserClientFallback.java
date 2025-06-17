package tn.health.telemedservice.Clients;

import org.springframework.stereotype.Component;
import tn.health.telemedservice.DTO.UserDTO;

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

    @Override
    public UserDTO getUserById(String id) {
        System.err.println("Fallback - returning mock user");

        if (id.equals("1")) {
            UserDTO user = new UserDTO();
            user.setId("1");
            user.setName("Mock Patient");
            user.setEmail("mock.patient@example.com");
            user.setRole("PATIENT");
            return user;
        }

        return null;
    }


}
