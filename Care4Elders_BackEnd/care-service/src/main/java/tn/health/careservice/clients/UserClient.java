package tn.health.careservice.clients;


import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tn.health.careservice.dto.UserDTO;

@EnableFeignClients
@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/users/{userId}")
    UserDTO getUserById(@PathVariable("userId") String userId);

}
