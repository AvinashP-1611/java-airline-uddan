package com.airline.system.controller;

import com.airline.system.entity.City;
import com.airline.system.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class CityController {

    @Autowired
    private CityRepository cityRepository;

    @GetMapping({ "/public/cities", "/admin/cities" })
    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    @PostMapping("/admin/cities")
    public City addCity(@RequestBody City city) {
        return cityRepository.save(city);
    }

    @DeleteMapping("/admin/cities/{id}")
    public void deleteCity(@PathVariable Long id) {
        cityRepository.deleteById(id);
    }
}
