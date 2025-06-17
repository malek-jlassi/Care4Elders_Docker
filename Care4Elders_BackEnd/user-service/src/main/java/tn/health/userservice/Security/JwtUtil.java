package tn.health.userservice.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tn.health.userservice.Entities.Utilisateur;

import java.util.Date;
@Component

public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expirationMs}")
    private long expirationMs;

    public String generateToken(Utilisateur utilisateur) {
        return Jwts.builder()
                .setSubject(utilisateur.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

}
