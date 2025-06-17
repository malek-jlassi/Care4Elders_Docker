package tn.health.telemedservice.DTO;

import lombok.Data;

@Data
public class UserDTO {
    private String id;
    private String name;
    private String role;

    private String email;
}
