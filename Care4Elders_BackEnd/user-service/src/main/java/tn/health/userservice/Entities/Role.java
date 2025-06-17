package tn.health.userservice.Entities;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,
    PATIENT,
    AIDANT,
    MEDECIN

;

    @Override
    public String getAuthority() {
        return name();
    }
}
