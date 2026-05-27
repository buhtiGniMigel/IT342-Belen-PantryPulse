package edu.cit.belen.pantrypulse.auth;

import edu.cit.belen.pantrypulse.user.User;
import edu.cit.belen.pantrypulse.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailVerificationService emailVerificationService;

    // ─── REGISTER ──────────────────────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use");
        }

        // user.getPassword() reads the @Transient 'password' field sent in the JSON
        String plainPassword = user.getPassword();
        if (plainPassword == null || plainPassword.isBlank()) {
            return ResponseEntity.badRequest().body("Password is required");
        }
        if (plainPassword.length() < 8) {
            return ResponseEntity.badRequest().body("Password must be at least 8 characters");
        }

        user.setPasswordHash(passwordEncoder.encode(plainPassword));

        // Generate email verification token
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setVerified(false);

        User saved = userRepository.save(user);
        emailVerificationService.sendVerificationEmail(saved.getEmail(), token);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Registration successful! Please check your email to verify your account.");
    }

    // ─── VERIFY EMAIL ──────────────────────────────────────────────────────────
    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        return userRepository.findByVerificationToken(token)
                .map(user -> {
                    user.setVerified(true);
                    user.setVerificationToken(null);
                    userRepository.save(user);
                    return ResponseEntity.ok().body("Email verified successfully! You can now log in.");
                })
                .orElse(ResponseEntity.badRequest().body("Invalid or expired verification token."));
    }

    // ─── LOGIN ─────────────────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        return userRepository.findByEmail(loginRequest.getEmail())
                .map(user -> {
                    if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
                    }
                    if (!user.isVerified()) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Please verify your email before logging in. Check your inbox.");
                    }
                    return ResponseEntity.ok().body(Map.of(
                            "message", "Login successful!",
                            "token", "mock-jwt-token",
                            "email", user.getEmail(),
                            "firstName", user.getFirstName()
                    ));
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password"));
    }

    // ─── GOOGLE OAUTH ──────────────────────────────────────────────────────────
    @PostMapping("/google")
    public ResponseEntity<?> googleAuth(@RequestBody Map<String, String> body) {
        String accessToken = body.get("accessToken");
        if (accessToken == null || accessToken.isBlank()) {
            return ResponseEntity.badRequest().body("Google access token is required");
        }

        try {
            // Verify token by calling Google's userinfo endpoint
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<Map> googleRes = restTemplate.exchange(
                    "https://www.googleapis.com/oauth2/v3/userinfo",
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> googleData = googleRes.getBody();
            if (googleData == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google token");
            }

            String email      = (String) googleData.get("email");
            String firstName  = (String) googleData.get("given_name");
            String lastName   = (String) googleData.get("family_name");
            String googleId   = (String) googleData.get("sub");

            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Could not retrieve email from Google");
            }

            final String fName = firstName != null ? firstName : "Google";
            final String lName = lastName  != null ? lastName  : "User";
            final String gId   = googleId;

            // Find existing user or create a new Google-linked account
            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setFirstName(fName);
                newUser.setLastName(lName);
                newUser.setGoogleOauthId(gId);
                newUser.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
                newUser.setVerified(true); // Google accounts are pre-verified
                return userRepository.save(newUser);
            });

            return ResponseEntity.ok().body(Map.of(
                    "message", "Google login successful!",
                    "token", "mock-jwt-token",
                    "email", user.getEmail(),
                    "firstName", user.getFirstName()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Google authentication failed: " + e.getMessage());
        }
    }

    // ─── RESEND VERIFICATION ───────────────────────────────────────────────────
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        return userRepository.findByEmail(email)
                .map(user -> {
                    if (user.isVerified()) {
                        return ResponseEntity.badRequest().body("This account is already verified");
                    }
                    String token = UUID.randomUUID().toString();
                    user.setVerificationToken(token);
                    userRepository.save(user);
                    emailVerificationService.sendVerificationEmail(email, token);
                    return ResponseEntity.ok().body("Verification email resent. Please check your inbox.");
                })
                .orElse(ResponseEntity.badRequest().body("No account found with that email"));
    }
}