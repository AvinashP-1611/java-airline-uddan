package com.airline.system.config;

import com.airline.system.entity.Airline;
import com.airline.system.entity.City;
import com.airline.system.entity.User;
import com.airline.system.repository.AirlineRepository;
import com.airline.system.repository.CityRepository;
import com.airline.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private AirlineRepository airlineRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin@123")); // User requested 'admin@123'
            admin.setRole(User.Role.ADMIN);
            admin.setFullName("System Administrator"); // Default full name
            admin.setEmail("admin@udaan.com"); // Default email
            admin.setPhone("0000000000");

            userRepository.save(admin);
            System.out.println("ADMIN User created successfully: admin / admin@123");
        }

        if (!userRepository.existsByUsername("testuser")) {
            User user = new User();
            user.setUsername("testuser");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole(User.Role.USER);
            user.setFullName("Test User");
            user.setEmail("test@example.com");
            userRepository.save(user);
            System.out.println("USER created successfully: testuser / password");
        }

        // Initialize Cities
        if (cityRepository.count() == 0) {
            cityRepository.save(new City("Kolkata", "CCU"));
            cityRepository.save(new City("Delhi", "DEL"));
            cityRepository.save(new City("Mumbai", "BOM"));
            cityRepository.save(new City("Bangalore", "BLR"));
            cityRepository.save(new City("Chennai", "MAA"));
            cityRepository.save(new City("Hyderabad", "HYD"));
            cityRepository.save(new City("Pune", "PNQ"));
            cityRepository.save(new City("Ahmedabad", "AMD"));
            System.out.println("Initial cities populated.");
        }

        // Initialize Airlines
        if (airlineRepository.count() == 0) {
            airlineRepository.save(new Airline("IndiGo"));
            airlineRepository.save(new Airline("Air India"));
            airlineRepository.save(new Airline("SpiceJet"));
            airlineRepository.save(new Airline("Vistara"));
            airlineRepository.save(new Airline("Akasa Air"));
            System.out.println("Initial airlines populated.");
        }
    }
}
