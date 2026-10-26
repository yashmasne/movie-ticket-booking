package com.movieticket.service;

import com.movieticket.dto.AuthResponse;
import com.movieticket.dto.LoginRequest;
import com.movieticket.dto.RegisterRequest;
import com.movieticket.entity.User;
import com.movieticket.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(("SALT_TICKET_" + password).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    public User register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + req.getEmail());
        }

        String role = (req.getRole() != null && req.getRole().equalsIgnoreCase("ROLE_ADMIN"))
                ? "ROLE_ADMIN" : "ROLE_USER";

        User user = new User(
                req.getFullName(),
                req.getEmail().toLowerCase().trim(),
                hashPassword(req.getPassword()),
                req.getPhone(),
                role
        );
        return userRepository.save(user);
    }

    public AuthResponse authenticate(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        String hashedInput = hashPassword(req.getPassword());
        if (!user.getPassword().equals(hashedInput)) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String mockToken = UUID.randomUUID().toString();
        return new AuthResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                mockToken
        );
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase().trim());
    }
}
