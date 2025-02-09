package org.example.weatherapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.weatherapp.model.Location;
import org.example.weatherapp.model.User;
import org.example.weatherapp.repository.LocationRepository;
import org.example.weatherapp.repository.UserRepository;
import org.example.weatherapp.results.LocationSearchRes;
import org.example.weatherapp.results.WeatherSearchRes;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    private final OpenWeatherApiService openWeatherApiService;


    public String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public List<LocationSearchRes> findCities(String city) throws URISyntaxException, IOException, InterruptedException {
        String response = openWeatherApiService.getStringHttpResponse(city);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<LocationSearchRes> locations = objectMapper.readValue(response, new TypeReference<>() {});

        if (!locations.isEmpty()) {
            return locations;
        } else {
            throw new IllegalArgumentException(city);
        }
    }


    @Transactional
    public void addLocation(String name, String country, String latitude, String longitude) {
        User user = userRepository.findByUsername(getCurrentUser());
        Location location = new Location(name, country, latitude, longitude, user);
        if (locationRepository.existsByLatitudeAndLongitudeAndUser(latitude, longitude, user)) {
            return;
        }
        locationRepository.saveAndFlush(location);
    }


    @Transactional
    public List<WeatherSearchRes> findTemperatures() throws URISyntaxException, IOException, InterruptedException {
        User user = userRepository.findByUsername(getCurrentUser());
        List<Location> foundLocations = locationRepository.findByUser(user);
        List<WeatherSearchRes> result = new ArrayList<>();

        for (Location location : foundLocations) {
            String weather = openWeatherApiService.getWeather(location.getLatitude(), location.getLongitude());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            WeatherSearchRes resWeather = objectMapper.readValue(weather, new TypeReference<>() {});

            resWeather.setName(location.getName());
            resWeather.setCountry(location.getCountry());
            resWeather.setLat(location.getLatitude());
            resWeather.setLon(location.getLongitude());
            String description = resWeather.getWeather().getFirst().getDescription();
            String upperCaseDescription = description.substring(0, 1).toUpperCase() + description.substring(1);
            resWeather.getWeather().getFirst().setDescription(upperCaseDescription);

            result.add(resWeather);
        }
        return result;
    }


    @Transactional
    public void delLocation(String latitude, String longitude) {
        User user = userRepository.findByUsername(getCurrentUser());
        locationRepository.deleteByLatitudeAndLongitudeAndUser(latitude, longitude, user);
    }
}