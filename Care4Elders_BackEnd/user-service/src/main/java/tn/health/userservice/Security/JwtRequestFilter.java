package tn.health.userservice.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@WebFilter(urlPatterns = "/api/*")  // Filtrer toutes les requêtes vers l'API
public class JwtRequestFilter extends OncePerRequestFilter {  // Hérite de OncePerRequestFilter

    @Value("${jwt.secret}")
    private String secretKey;

    // Cette méthode doit être correctement surchargée à partir de OncePerRequestFilter
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String token = extractToken(request);  // Extraire le token depuis la requête
        if (token != null && validateToken(token)) {
            Claims claims = parseClaims(token);  // Extraire les informations du token
            String username = claims.getSubject();  // Obtenir le nom d'utilisateur

            // Authentifier l'utilisateur si le token est valide
            // Utiliser Spring Security pour l'authentification de l'utilisateur (exemple non complet)
            // SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Passer la requête au filtre suivant dans la chaîne
        chain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        // Extraire le token de l'en-tête Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);  // Retirer "Bearer " pour obtenir le token
        }
        return null;
    }

    private boolean validateToken(String token) {
        try {
            // Valider le token (vérification de l'expiration, signature...)
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        // Extraire les informations du token
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }
}
