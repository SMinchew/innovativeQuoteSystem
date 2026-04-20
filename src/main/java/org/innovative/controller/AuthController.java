package org.innovative.controller;

import org.innovative.model.User;
import org.innovative.repository.UserRepository;
import org.innovative.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.innovative.services.EmailService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Value("${app.company.code}")
    private String companyCode;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        // Check company code
        if (!companyCode.equals(request.get("companyCode"))) {
            return ResponseEntity.badRequest().body("Invalid company code");
        }

        // Check username taken
        if (userRepository.findByUsername(request.get("username")).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        // Check email taken
        if (userRepository.findByEmail(request.get("email")).isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        // Create user
        String token = java.util.UUID.randomUUID().toString();
        User user = new User();
        user.setUsername(request.get("username"));
        user.setPassword(passwordEncoder.encode(request.get("password")));
        user.setEmail(request.get("email"));
        user.setRole("USER");
        user.setEnabled(false);
        user.setVerificationToken(token);

        userRepository.save(user);

        // Send verification email
        emailService.sendVerificationEmail(request.get("email"), token);

        return ResponseEntity.ok("Registration successful. Please check your email to verify your account.");
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String token) {
        return userRepository.findByVerificationToken(token).map(user -> {
            user.setEnabled(true);
            user.setVerificationToken(null);
            userRepository.save(user);
            return ResponseEntity.ok("Email verified successfully. You can now log in.");
        }).orElse(ResponseEntity.badRequest().body("Invalid or expired verification token"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        User user = userRepository.findByUsername(request.get("username"))
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        if (!user.isEnabled()) {
            return ResponseEntity.status(403).body("Please verify your email before logging in");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.get("username"),
                        request.get("password")
                )
        );

        String token = jwtUtil.generateToken(request.get("username"));
        return ResponseEntity.ok(Map.of("token", token));
    }
}
