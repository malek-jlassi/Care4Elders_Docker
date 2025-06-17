package tn.health.appointmentservice.clients;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tn.health.appointmentservice.DTO.UserDTO;


import java.util.List;

@FeignClient(name = "user-service", url = "http://localhost:8080", fallback = UserClientFallback.class)
public interface UserClient {

    @GetMapping("/users")
    List<UserDTO> getAllUsers();
    @GetMapping("/users/by-role")
    List<UserDTO> getUserByRole(@RequestParam String role);

}
