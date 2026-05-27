package edu.cit.belen.pantrypulse.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailVerificationService {

    // Optional — null if SMTP credentials are not configured
    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${spring.mail.from:noreply@pantrypulse.com}")
    private String fromEmail;

    public void sendVerificationEmail(String toEmail, String token) {
        String verificationLink = baseUrl + "/api/auth/verify?token=" + token;

        if (mailSender == null) {
            // Dev fallback: print to console when SMTP is not configured
            System.out.println("╔══════════════════════════════════════════════════╗");
            System.out.println("║  [DEV] Email verification (SMTP not configured)  ║");
            System.out.println("╠══════════════════════════════════════════════════╣");
            System.out.println("║  To: " + toEmail);
            System.out.println("║  Link: " + verificationLink);
            System.out.println("╚══════════════════════════════════════════════════╝");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("PantryPulse — Verify Your Email Address");
            message.setText(
                "Hi there!\n\n" +
                "Welcome to PantryPulse! Please verify your email address by clicking the link below:\n\n" +
                verificationLink + "\n\n" +
                "This link is valid for 24 hours.\n\n" +
                "If you did not create a PantryPulse account, you can safely ignore this email.\n\n" +
                "— The PantryPulse Team"
            );
            mailSender.send(message);
            System.out.println("[EMAIL] Verification email sent to: " + toEmail);
        } catch (Exception e) {
            System.err.println("[EMAIL ERROR] Failed to send to " + toEmail + ": " + e.getMessage());
            System.out.println("[DEV FALLBACK] Verification link: " + verificationLink);
        }
    }
}
