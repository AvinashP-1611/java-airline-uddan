package com.airline.system.service;

import com.airline.system.entity.Flight;
import com.airline.system.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private com.airline.system.repository.BookingRepository bookingRepository;

    public Flight addFlight(Flight flight) {
        if (flightRepository.findByFlightNumber(flight.getFlightNumber()).isPresent()) {
            throw new RuntimeException("Flight number already exists!");
        }
        // Initialize available seats to total for both classes
        flight.setEconomySeatsAvailable(flight.getEconomySeatsTotal());
        flight.setBusinessSeatsAvailable(flight.getBusinessSeatsTotal());
        return flightRepository.save(flight);
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Flight getFlightById(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found with ID: " + id));
    }

    public Flight updateFlight(Long id, Flight flightDetails) {
        Flight flight = getFlightById(id);

        flight.setAirlineName(flightDetails.getAirlineName());
        flight.setOrigin(flightDetails.getOrigin());
        flight.setDestination(flightDetails.getDestination());
        flight.setDepartureTime(flightDetails.getDepartureTime());
        flight.setArrivalTime(flightDetails.getArrivalTime());
        flight.setEconomyPrice(flightDetails.getEconomyPrice());
        flight.setBusinessPrice(flightDetails.getBusinessPrice());
        flight.setEconomySeatsTotal(flightDetails.getEconomySeatsTotal());
        flight.setBusinessSeatsTotal(flightDetails.getBusinessSeatsTotal());
        flight.setStatus(flightDetails.getStatus());

        return flightRepository.save(flight);
    }

    public void deleteFlight(Long id) {
        if (bookingRepository.existsByFlightId(id)) {
            throw new RuntimeException("Cannot delete flight: Users have already booked this flight.");
        }
        flightRepository.deleteById(id);
    }

    // Enhanced Search
    public List<Flight> searchFlights(String origin, String destination, java.time.LocalDate searchDate) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        List<Flight> flights;

        if (searchDate != null) {
            java.time.LocalDateTime startOfDay = searchDate.atStartOfDay();
            java.time.LocalDateTime endOfDay = searchDate.atTime(23, 59, 59);
            // If searching for today, the start time should be 'now' to filter past flights
            if (searchDate.equals(now.toLocalDate())) {
                startOfDay = now;
            }
            flights = flightRepository.findByOriginAndDestinationAndDepartureTimeBetweenAndStatus(
                    origin, destination, startOfDay, endOfDay, Flight.Status.SCHEDULED);
        } else {
            // Default: Search from 'now' onwards for this route
            flights = flightRepository.findByOriginAndDestinationAndStatus(origin, destination,
                    Flight.Status.SCHEDULED);
        }

        // Final filter in stream just in case (e.g. if searchDate was null or logic
        // missed it)
        return flights.stream()
                .filter(f -> f.getDepartureTime() != null && f.getDepartureTime().isAfter(now))
                .toList();
    }

    public String generateNextFlightNumber() {
        Flight lastFlight = flightRepository.findTopByOrderByIdDesc();
        if (lastFlight == null) {
            return "FL0001";
        }
        String lastNum = lastFlight.getFlightNumber();
        if (lastNum != null && lastNum.startsWith("FL")) {
            try {
                int num = Integer.parseInt(lastNum.substring(2));
                return String.format("FL%04d", num + 1);
            } catch (Exception e) {
                // Ignore and fallback
            }
        }
        return "FL0001";
    }

    public List<Flight> getFlightsByAirline(String airlineName) {
        return flightRepository.findByAirlineName(airlineName);
    }

    public Flight updateFlightSchedule(Long id, Flight scheduleDetails) {
        Flight flight = getFlightById(id);
        flight.setDepartureTime(scheduleDetails.getDepartureTime());
        flight.setArrivalTime(scheduleDetails.getArrivalTime());
        // Allow updating prices as well
        if (scheduleDetails.getEconomyPrice() != null)
            flight.setEconomyPrice(scheduleDetails.getEconomyPrice());
        if (scheduleDetails.getBusinessPrice() != null)
            flight.setBusinessPrice(scheduleDetails.getBusinessPrice());
        return flightRepository.save(flight);
    }
}
