package com.airline.system.repository;

import com.airline.system.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    Optional<Flight> findByFlightNumber(String flightNumber);

    // Search methods for User
    List<Flight> findByOriginAndDestinationAndStatus(String origin, String destination, Flight.Status status);

    List<Flight> findByOriginAndDestinationAndDepartureTimeBetweenAndStatus(
            String origin, String destination, LocalDateTime start, LocalDateTime end, Flight.Status status);

    List<Flight> findByStatus(Flight.Status status);

    Flight findTopByOrderByIdDesc();

    List<Flight> findByAirlineName(String airlineName);
}
