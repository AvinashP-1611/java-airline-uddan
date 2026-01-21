package com.airline.system.repository;

import com.airline.system.entity.Booking;
import com.airline.system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);

    List<Booking> findByUserId(Long userId);

    org.springframework.data.domain.Page<Booking> findByUserId(Long userId,
            org.springframework.data.domain.Pageable pageable);

    List<Booking> findByFlightAirlineName(String airlineName);

    org.springframework.data.domain.Page<Booking> findByFlightAirlineName(String airlineName,
            org.springframework.data.domain.Pageable pageable);

    boolean existsByFlightId(Long flightId);
}
