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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    @Value("${open_weather.api.key}")
    private String apiKey;

    @Value("${open_weather.api.inf.url}")
    private String infUrl;

    @Value("${open_weather.api.geo.url}")
    private String geoUrl;

    public String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Transactional
    public List<LocationSearchRes> findCities(String city) throws URISyntaxException, IOException, InterruptedException {
        String cityGeoUrlEd = String.format("%s?q=%s&limit=5&appid=%s", geoUrl, city, apiKey);
        String cityGeoUrl = cityGeoUrlEd.replace(" ", "+");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(cityGeoUrl))
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<LocationSearchRes> locations = objectMapper.readValue(response.body(), new TypeReference<List<LocationSearchRes>>() {});

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
            String weather = getWeather(location.getLatitude(), location.getLongitude());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            WeatherSearchRes resWeather = objectMapper.readValue(weather, new TypeReference<WeatherSearchRes>() {});

            resWeather.setName(location.getName());
            resWeather.setCountry(location.getCountry());
            resWeather.setLat(location.getLatitude());
            resWeather.setLon(location.getLongitude());
            String descrip = resWeather.getWeather().getFirst().getDescription();
            String upperCaseDescription = descrip.substring(0, 1).toUpperCase() + descrip.substring(1);
            resWeather.getWeather().getFirst().setDescription(upperCaseDescription);

            result.add(resWeather);
        }
        return result;
    }


    public String getWeather(String latitude, String longitude) throws URISyntaxException, IOException, InterruptedException {
        String weatherUrl = String.format("%s?lat=%s&lon=%s&units=metric&appid=%s", infUrl, latitude, longitude, apiKey);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(weatherUrl))
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    @Transactional
    public void delLocation(String latitude, String longitude) {
        User user = userRepository.findByUsername(getCurrentUser());
        locationRepository.deleteByLatitudeAndLongitudeAndUser(latitude, longitude, user);
    }
}