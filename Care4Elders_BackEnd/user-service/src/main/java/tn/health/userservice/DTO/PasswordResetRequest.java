package tn.health.userservice.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequest {
    private String token;
    private String nouveauMotDePasse;
}
