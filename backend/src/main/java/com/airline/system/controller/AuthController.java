package com.airline.system.controller;

import com.airline.system.entity.User;
import com.airline.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    /**
     * API to Register a new User
     * Endpoint: POST /api/auth/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.ok("User registered successfully via ID: " + registeredUser.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * API to Login User
     * Endpoint: POST /api/auth/login
     * Note: In a real app, this would return a JWT Token.
     * For simplicity, we are just verifying credentials here.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userService.getUserByUsername(loginRequest.getUsername()).orElseThrow();
            return ResponseEntity.ok(new LoginResponse(
                    "Login successful!",
                    user.getRole().toString(),
                    user.getAirlineName()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    // DTO for Login Response
    public static class LoginResponse {
        private String message;
        private String role;
        private String airlineName;

        public LoginResponse(String m, String r, String a) {
            this.message = m;
            this.role = r;
            this.airlineName = a;
        }

        public String getMessage() {
            return message;
        }

        public String getRole() {
            return role;
        }

        public String getAirlineName() {
            return airlineName;
        }
    }

    // DTO for Login
    public static class LoginRequest {
        private String username;
        private String password;

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
