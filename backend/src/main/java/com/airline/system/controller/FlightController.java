package com.airline.system.controller;

import com.airline.system.entity.Flight;
import com.airline.system.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class FlightController {

    @Autowired
    private FlightService flightService;

    // --- Admin Endpoints ---

    @PostMapping("/admin/flights")
    public ResponseEntity<Flight> addFlight(@RequestBody Flight flight) {
        return ResponseEntity.ok(flightService.addFlight(flight));
    }

    @PutMapping("/admin/flights/{id}")
    public ResponseEntity<Flight> updateFlight(@PathVariable Long id, @RequestBody Flight flight) {
        return ResponseEntity.ok(flightService.updateFlight(id, flight));
    }

    @DeleteMapping("/admin/flights/{id}")
    public ResponseEntity<?> deleteFlight(@PathVariable Long id) {
        try {
            flightService.deleteFlight(id);
            return ResponseEntity.ok("Flight deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/admin/flights/next-number")
    public ResponseEntity<String> getNextFlightNumber() {
        return ResponseEntity.ok(flightService.generateNextFlightNumber());
    }

    // --- Airline Role Endpoints ---

    @GetMapping("/airline/flights")
    public List<Flight> getAirlineFlights(@RequestParam String airlineName) {
        return flightService.getFlightsByAirline(airlineName);
    }

    @PutMapping("/airline/flights/{id}/schedule")
    public ResponseEntity<Flight> updateSchedule(@PathVariable Long id, @RequestBody Flight schedule) {
        return ResponseEntity.ok(flightService.updateFlightSchedule(id, schedule));
    }

    // --- Public/User Endpoints ---

    @GetMapping("/public/flights")
    public List<Flight> getAllFlights() {
        return flightService.getAllFlights();
    }

    @GetMapping("/public/flights/{id}")
    public ResponseEntity<Flight> getFlightById(@PathVariable Long id) {
        return ResponseEntity.ok(flightService.getFlightById(id));
    }

    @GetMapping("/public/flights/search")
    public List<Flight> searchFlights(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date) {
        return flightService.searchFlights(origin, destination, date);
    }
}
