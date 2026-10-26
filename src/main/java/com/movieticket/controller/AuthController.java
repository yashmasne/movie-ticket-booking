package com.movieticket.controller;

import com.movieticket.dto.ApiResponse;
import com.movieticket.dto.AuthResponse;
import com.movieticket.dto.LoginRequest;
import com.movieticket.dto.RegisterRequest;
import com.movieticket.entity.User;
import com.movieticket.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest req) {
        try {
            User user = userService.register(req);
            return ResponseEntity.ok(ApiResponse.ok("Registration successful! You can now log in.", user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest req) {
        try {
            AuthResponse response = userService.authenticate(req);
            return ResponseEntity.ok(ApiResponse.ok("Login successful!", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Authentication error: " + e.getMessage()));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile(@RequestParam(required = false) Long id,
                                                  @RequestParam(required = false) String email) {
        if (id != null) {
            return userService.findById(id)
                    .map(u -> ResponseEntity.ok(ApiResponse.ok("User found", u)))
                    .orElse(ResponseEntity.notFound().build());
        } else if (email != null) {
            return userService.findByEmail(email)
                    .map(u -> ResponseEntity.ok(ApiResponse.ok("User found", u)))
                    .orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.badRequest().body(ApiResponse.error("Provide id or email"));
    }
}
