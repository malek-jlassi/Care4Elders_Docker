package tn.health.telemedservice.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendTeleconsultationEmail(String to, String date, String link) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("care4elders2025@gmail.com"); // 🔴 Remplace par l'adresse dans spring.mail.username
            helper.setTo(to);
            helper.setSubject("Votre téléconsultation est programmée");
            helper.setText("Bonjour,\n\nVotre téléconsultation est prévue pour le " + date +
                    ".\nVoici le lien : " + link + "\n\nCordialement,\nCare4Elders");

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }
}