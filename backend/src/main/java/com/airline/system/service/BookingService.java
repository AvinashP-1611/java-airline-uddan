package com.airline.system.service;

import com.airline.system.entity.Booking;
import com.airline.system.entity.Flight;
import com.airline.system.entity.Passenger;
import com.airline.system.entity.User;
import com.airline.system.repository.BookingRepository;
import com.airline.system.repository.FlightRepository;
import com.airline.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletService walletService;

    @Transactional
    public Booking bookFlight(Long userId, Long flightId, String flightClass, List<Passenger> passengers,
            String paymentMethod, Double totalPrice) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + flightId));

        int passengerCount = passengers.size();
        if ("BUSINESS".equalsIgnoreCase(flightClass)) {
            if (flight.getBusinessSeatsAvailable() < passengerCount) {
                throw new RuntimeException("Not enough Business class seats available. Required: " + passengerCount);
            }
            flight.setBusinessSeatsAvailable(flight.getBusinessSeatsAvailable() - passengerCount);
        } else {
            if (flight.getEconomySeatsAvailable() < passengerCount) {
                throw new RuntimeException("Not enough Economy class seats available. Required: " + passengerCount);
            }
            flight.setEconomySeatsAvailable(flight.getEconomySeatsAvailable() - passengerCount);
        }
        flightRepository.save(flight);

        // Create booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setFlightClass(flightClass.toUpperCase());
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setPaymentMethod(paymentMethod);
        booking.setTotalPrice(totalPrice);
        booking.setTransactionId("TXN-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 1000));

        // Link passengers to booking
        for (Passenger p : passengers) {
            p.setBooking(booking);
        }
        booking.setPassengers(passengers);

        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public org.springframework.data.domain.Page<Booking> getBookingsByUserId(Long userId,
            org.springframework.data.domain.Pageable pageable) {
        return bookingRepository.findByUserId(userId, pageable);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public org.springframework.data.domain.Page<Booking> getAllBookings(
            org.springframework.data.domain.Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }

    public List<Booking> getBookingsByAirlineName(String airlineName) {
        return bookingRepository.findByFlightAirlineName(airlineName);
    }

    public org.springframework.data.domain.Page<Booking> getBookingsByAirlineName(String airlineName,
            org.springframework.data.domain.Pageable pageable) {
        return bookingRepository.findByFlightAirlineName(airlineName, pageable);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }

    public List<Booking> getRecentBookings(int limit) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                0, limit, org.springframework.data.domain.Sort.by("bookingTime").descending());
        return bookingRepository.findAll(pageable).getContent();
    }

    public List<Booking> getRecentBookingsByAirline(String airlineName, int limit) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                0, limit, org.springframework.data.domain.Sort.by("bookingTime").descending());
        return bookingRepository.findByFlightAirlineName(airlineName, pageable).getContent();
    }

    public java.util.Map<String, Object> getAirlineStats(String airlineName) {
        long totalBookings = bookingRepository.countByFlightAirlineName(airlineName);
        Double totalRevenue = bookingRepository.sumTotalPriceByAirlineAndStatus(airlineName,
                Booking.BookingStatus.CONFIRMED);

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalBookings", totalBookings);
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
        return stats;
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }

        // Increase available seats based on class and passenger count
        int passengerCount = booking.getPassengers().size();
        Flight flight = booking.getFlight();
        if ("BUSINESS".equalsIgnoreCase(booking.getFlightClass())) {
            flight.setBusinessSeatsAvailable(flight.getBusinessSeatsAvailable() + passengerCount);
        } else {
            flight.setEconomySeatsAvailable(flight.getEconomySeatsAvailable() + passengerCount);
        }
        flightRepository.save(flight);

        // Update booking status
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Process Refund to Wallet
        walletService.addFunds(booking.getUser(), booking.getTotalPrice(),
                "Refund for cancelled booking #" + booking.getId() + " (" + flight.getFlightNumber() + ")");
    }
}
