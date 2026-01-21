package com.airline.system.controller;

import com.airline.system.entity.Airline;
import com.airline.system.repository.AirlineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AirlineController {

    @Autowired
    private AirlineRepository airlineRepository;

    @GetMapping("/public/airlines")
    public List<Airline> getAllAirlines() {
        return airlineRepository.findAll();
    }

    @PostMapping("/admin/airlines")
    public Airline addAirline(@RequestBody Airline airline) {
        return airlineRepository.save(airline);
    }

    @PutMapping("/admin/airlines/{id}")
    public Airline updateAirline(@PathVariable Long id, @RequestBody Airline airlineDetails) {
        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Airline not found"));
        airline.setName(airlineDetails.getName());
        return airlineRepository.save(airline);
    }

    @DeleteMapping("/admin/airlines/{id}")
    public void deleteAirline(@PathVariable Long id) {
        airlineRepository.deleteById(id);
    }
}
