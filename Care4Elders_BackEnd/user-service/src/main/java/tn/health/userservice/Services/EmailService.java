package tn.health.userservice.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.health.userservice.Entities.PasswordResetToken;
import tn.health.userservice.Entities.Utilisateur;
import tn.health.userservice.Repositories.PasswordResetTokenRepository;
import tn.health.userservice.Repositories.UtilisateurRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public void sendResetPasswordEmail(String to, String token) {
        String subject = "Réinitialisation du mot de passe";
        String resetLink = "http://localhost:4200/utilisateur/forgot-password?token=" + token;

        String htmlContent = """
                    <html>
                      <body style="margin:0; padding:0; background-color:#f5f5f5; font-family: Arial, sans-serif;">
                        <div style="max-width:600px; margin:40px auto; background-color:#ffffff; border-radius:10px; padding:30px; box-shadow:0 0 10px rgba(0,0,0,0.1); text-align:center;">
                          <img src="https://www.gstatic.com/images/branding/product/1x/mail_48dp.png" alt="Care4Elders Logo" style="width:50px; margin-bottom:20px;">
                          <h2 style="color:#202124;">Réinitialisation de votre mot de passe</h2>
                          <p style="color:#5f6368; font-size:16px; line-height:1.5;">
                            Bonjour,<br><br>
                            Vous avez demandé à réinitialiser votre mot de passe. Cliquez sur le bouton ci-dessous pour définir un nouveau mot de passe.
                          </p>
                          <a href="%s" style="display:inline-block; margin:30px 0; padding:12px 24px; background-color:#1a73e8; color:#ffffff; text-decoration:none; border-radius:5px; font-size:16px;">
                            Réinitialiser le mot de passe
                          </a>
                          <p style="color:#5f6368; font-size:14px;">
                            ⚠️ Ce lien est valable pendant 30 minutes.
                          </p>
                          <hr style="margin:30px 0; border:none; border-top:1px solid #dadce0;">
                          <p style="font-size:12px; color:#5f6368;">
                            Si vous n'avez pas fait cette demande, vous pouvez ignorer cet e-mail en toute sécurité.<br>
                            Merci, <br><strong>L'équipe Care4Elders</strong>
                          </p>
                        </div>
                      </body>
                    </html>
                """.formatted(resetLink);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true pour HTML

            mailSender.send(message);
            System.out.println("Email de réinitialisation envoyé à " + to);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
        }
    }

    public void resetPassword(String email) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findByEmail(email);
        if (utilisateur.isEmpty()) {
            throw new RuntimeException("Aucun utilisateur trouvé avec cet email.");
        }

        String token = UUID.randomUUID().toString();
        LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(30);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .email(email)
                .token(token)
                .expirationDate(expirationDate)
                .build();

        passwordResetTokenRepository.deleteByEmail(email); // Supprimer l'ancien token s'il existe
        passwordResetTokenRepository.save(resetToken);

        // ✅ Envoi du mail avec contenu personnalisé
        sendResetPasswordEmail(email, token);
    }

    public void updatePasswordWithToken(String token, String nouveauMotDePasse) {
        Optional<PasswordResetToken> resetToken = passwordResetTokenRepository.findByToken(token);
        
        if (resetToken.isEmpty()) {
            throw new RuntimeException("Token invalide.");
        }

        if (resetToken.get().getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Le token a expiré.");
        }

        Optional<Utilisateur> utilisateur = utilisateurRepository.findByEmail(resetToken.get().getEmail());
        if (utilisateur.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé.");
        }

        utilisateur.get().setPassword(passwordEncoder.encode(nouveauMotDePasse));
        utilisateurRepository.save(utilisateur.get());
        passwordResetTokenRepository.deleteByToken(token);
    }
}
