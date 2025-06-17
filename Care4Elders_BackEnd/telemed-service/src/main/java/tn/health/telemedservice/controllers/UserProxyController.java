package tn.health.telemedservice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import tn.health.telemedservice.Clients.UserClient;
import tn.health.telemedservice.DTO.UserDTO;

import java.util.List;

public class UserProxyController {

    private UserClient userClient;

    @GetMapping("/doctors")
    public List<UserDTO> getDoctors() {
        return userClient.getUserByRole("DOCTOR");
    }

    @GetMapping("/patients")
    public List<UserDTO> getPatients() {
        return userClient.getUserByRole("PATIENT");
    }
}
