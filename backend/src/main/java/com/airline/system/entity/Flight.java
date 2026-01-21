package com.airline.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "flights")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_number", nullable = false, unique = true)
    private String flightNumber;

    @Column(name = "airline_name", nullable = false)
    private String airlineName;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Column(name = "departure_time", nullable = true)
    private LocalDateTime departureTime;

    @Column(name = "arrival_time", nullable = true)
    private LocalDateTime arrivalTime;

    @Column(name = "economy_price", nullable = false)
    private BigDecimal economyPrice;

    @Column(name = "business_price", nullable = false)
    private BigDecimal businessPrice;

    @Column(name = "economy_seats_total", nullable = false)
    private int economySeatsTotal;

    @Column(name = "economy_seats_available", nullable = false)
    private int economySeatsAvailable;

    @Column(name = "business_seats_total", nullable = false)
    private int businessSeatsTotal;

    @Column(name = "business_seats_available", nullable = false)
    private int businessSeatsAvailable;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.SCHEDULED;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum Status {
        SCHEDULED, CANCELLED, DELAYED
    }
}
