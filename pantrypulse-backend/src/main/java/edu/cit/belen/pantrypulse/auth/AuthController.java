package edu.cit.belen.pantrypulse.auth;

import edu.cit.belen.pantrypulse.user.User;
import edu.cit.belen.pantrypulse.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        return userRepository.findByEmail(loginRequest.getEmail())
                .filter(u -> passwordEncoder.matches(loginRequest.getPassword(), u.getPasswordHash()))
                .map(u -> ResponseEntity.ok().body(java.util.Map.of("token", "mock-jwt-token", "email", u.getEmail())))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}