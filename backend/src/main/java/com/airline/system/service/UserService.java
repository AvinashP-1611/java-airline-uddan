package com.airline.system.service;

import com.airline.system.entity.User;
import com.airline.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register a new user (default ROLE_USER)
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        // Handle missing fields for simplified signup (User Request)
        // If email/fullName are null, we auto-generate them to satisfy DB constraints
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            // Check if auto-generated email exists? Unlikely with timestamp/unique username
            user.setEmail(user.getUsername() + "@example.com");
        }
        if (user.getFullName() == null || user.getFullName().isEmpty()) {
            user.setFullName(user.getUsername());
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            // Only verify if we passed a real email or if our dummy clashes
            throw new RuntimeException("Email is already in use!");
        }

        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Default role if not provided
        if (user.getRole() == null) {
            user.setRole(User.Role.USER);
        }

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setFullName(userDetails.getFullName());
            user.setPhone(userDetails.getPhone());
            user.setEmail(userDetails.getEmail());
            // Only update password if provided and non-empty
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> getUsersExcludingAdmin() {
        return userRepository.findByRoleNot(User.Role.ADMIN);
    }

    public User adminCreateUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // If it's an airline, we might want to validate airline name exists (optional)

        return userRepository.save(user);
    }
}
