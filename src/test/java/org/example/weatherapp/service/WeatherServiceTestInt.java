package org.example.weatherapp.service;

import org.example.weatherapp.model.Location;
import org.example.weatherapp.model.User;
import org.example.weatherapp.repository.LocationRepository;
import org.example.weatherapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class WeatherServiceIntTest {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        locationRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("testPassword");
        userRepository.save(testUser);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testUser.getUsername(), null)
        );
    }

    @Test
    public void testAddLocation() {
        weatherService.addLocation("London", "GB", "51.5073219", "-0.1276474");

        Location savedLocation = locationRepository.findByLatitudeAndLongitudeAndUser("51.5073219", "-0.1276474", testUser);
        assertNotNull(savedLocation);
        assertEquals("London", savedLocation.getName());
    }

}