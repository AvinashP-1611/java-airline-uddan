package com.airline.system.controller;

import com.airline.system.entity.Booking;
import com.airline.system.entity.Passenger;
import com.airline.system.entity.User;
import com.airline.system.repository.UserRepository;
import com.airline.system.service.BookingService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/user/bookings/book")
    public ResponseEntity<?> bookFlight(@RequestBody Map<String, Object> payload) {
        try {
            Long flightId = ((Number) payload.get("flightId")).longValue();
            String flightClass = (String) payload.get("flightClass");
            if (flightClass == null)
                flightClass = "ECONOMY";

            List<Passenger> passengers = objectMapper.convertValue(
                    payload.get("passengers"),
                    new TypeReference<List<Passenger>>() {
                    });

            if (passengers == null || passengers.isEmpty()) {
                return ResponseEntity.badRequest().body("Passenger details are required");
            }

            String paymentMethod = (String) payload.get("paymentMethod");
            Double totalPrice = ((Number) payload.get("totalPrice")).doubleValue();

            // Get current logged in user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            Booking booking = bookingService.bookFlight(user.getId(), flightId, flightClass, passengers, paymentMethod,
                    totalPrice);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/user/bookings/my")
    public ResponseEntity<?> getMyBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                    page, size, org.springframework.data.domain.Sort.by("bookingTime").descending());
            return ResponseEntity.ok(bookingService.getBookingsByUserId(user.getId(), pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/user/bookings/cancel/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        try {
            bookingService.cancelBooking(id);
            return ResponseEntity.ok("Booking cancelled successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/admin/bookings")
    public ResponseEntity<?> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page, size, org.springframework.data.domain.Sort.by("bookingTime").descending());
        return ResponseEntity.ok(bookingService.getAllBookings(pageable));
    }

    @GetMapping("/airline/bookings")
    public ResponseEntity<?> getAirlineBookings(
            @RequestParam String airlineName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page, size, org.springframework.data.domain.Sort.by("bookingTime").descending());
        return ResponseEntity.ok(bookingService.getBookingsByAirlineName(airlineName, pageable));
    }
}
