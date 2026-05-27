package edu.cit.belen.pantrypulse.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String googleOauthId;

    private boolean verified = false;

    private String verificationToken;

    private LocalDateTime createdAt = LocalDateTime.now();

    // ✅ Transient — used for incoming JSON only, NOT saved to DB
    @Transient
    private String password;

    // Getters and Setters
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public String getGoogleOauthId() { return googleOauthId; }
    public void setGoogleOauthId(String googleOauthId) { this.googleOauthId = googleOauthId; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public String getVerificationToken() { return verificationToken; }
    public void setVerificationToken(String verificationToken) { this.verificationToken = verificationToken; }

    // ✅ Transient password getter/setter
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}