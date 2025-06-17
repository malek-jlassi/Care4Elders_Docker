package tn.health.userservice.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.health.userservice.Entities.Utilisateur;
import tn.health.userservice.Repositories.UtilisateurRepository;
import tn.health.userservice.Security.JwtUtil;

import java.util.Optional;
@Service
public class AuthenticationService {
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    //@Autowired
    //private LoginRequestRepository loginRequestRepository; // Repository pour LoginRequest

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String login(String email, String password) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);
        if (!utilisateurOpt.isPresent()) {
            throw new RuntimeException("Utilisateur non trouvé !");
        }

        Utilisateur utilisateur = utilisateurOpt.get();
        if (!passwordEncoder.matches(password, utilisateur.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect !");
        }

        // Générer un token JWT
        return jwtUtil.generateToken(utilisateur);
    }

    public Utilisateur getUserByEmail(String email) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);
        return utilisateurOpt.orElse(null); // Retourne l'utilisateur ou null si non trouvé
    }
}

