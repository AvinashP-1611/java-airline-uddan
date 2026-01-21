package com.airline.system.controller;

import com.airline.system.entity.User;
import com.airline.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/admin/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * API to Get All Users (Admin Only)
     * Endpoint: GET /api/admin/users
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getUsersExcludingAdmin();
    }

    /**
     * API to Add New User/Airline (Admin Only)
     * Endpoint: POST /api/admin/users
     */
    @PostMapping
    public ResponseEntity<User> adminAddUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.adminCreateUser(user));
    }

    /**
     * API to Get User by ID
     * Endpoint: GET /api/admin/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * API to Update User
     * Endpoint: PUT /api/admin/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * API to Delete User
     * Endpoint: DELETE /api/admin/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully.");
    }
}
