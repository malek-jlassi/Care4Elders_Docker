package tn.health.userservice.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tn.health.userservice.Entities.Role;
import tn.health.userservice.Entities.Utilisateur;
import tn.health.userservice.Services.ServiceUtilisateur;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initAdmin(ServiceUtilisateur serviceUtilisateur) {
        return args -> {
            if (serviceUtilisateur.getAllUsersByRole(Role.ADMIN).isEmpty()) {
                Utilisateur admin = new Utilisateur();
                admin.setUsername("Salma Chennoufi");
                admin.setEmail("salma.chennoufi@gmail.com");
                admin.setPhoneNumber("22222222");
                admin.setPassword("Admincare4");
                admin.setRole(Role.ADMIN);

                Utilisateur savedAdmin = serviceUtilisateur.inscriptionUtilisateur(admin);

                if (savedAdmin != null && savedAdmin.getId() != null) {
                    System.out.println("👑 Administrateur créé et sauvegardé avec succès ! ID: " + savedAdmin.getId());
                } else {
                    System.err.println("❌ ECHEC: La sauvegarde de l'administrateur a échoué !");
                }
            } else {
                System.out.println("👑 Administrateur déjà existant. Aucune création.");
            }
        };
    }
}