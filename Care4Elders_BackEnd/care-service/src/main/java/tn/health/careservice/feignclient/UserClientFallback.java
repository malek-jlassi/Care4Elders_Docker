package tn.health.careservice.feignclient;



import tn.health.careservice.dto.UserDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public UserDTO getUserById(String id) {
        // You can customize this to return null, a default object, or throw an exception
        System.err.println("Fallback: Unable to fetch user with ID = " + id);
        return new UserDTO("N/A", "Unavailable", "N/A", 0, "unavailable@example.com");
    }


    public List<UserDTO> getAllUsers() {
        System.err.println("Fallback: Unable to fetch users list.");
        return Collections.emptyList();
    }


    public List<UserDTO> getUsersByRole(String role) {
        System.err.println("Fallback: Unable to fetch users by role: " + role);
        return Collections.emptyList();
    }
}

