package org.example.weatherapp.service;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.example.weatherapp.model.Location;
import org.example.weatherapp.model.User;
import org.example.weatherapp.repository.LocationRepository;
import org.example.weatherapp.repository.UserRepository;
import org.example.weatherapp.results.LocationSearchRes;
import org.example.weatherapp.results.WeatherSearchRes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class WeatherServiceTest {

    @InjectMocks
    private WeatherService weatherService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private OpenWeatherApiService openWeatherApiService;

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
    public void testFindCities() throws URISyntaxException, IOException, InterruptedException {
        String mockResponse = """
                [
                        {
                            "name":"London",
                            "local_names":{
                               "cv":"Лондон",
                               "az":"London",
                               "ru":"Лондон"
                            },
                            "lat":51.5073219,
                            "lon":-0.1276474,
                            "country":"GB",
                            "state":"England"
                        },
                        {
                            "name":"City of London",
                            "local_names":{
                               "es":"City de Londres",
                               "ru":"Сити",
                               "en":"City of London"
                            },
                            "lat":51.5156177,
                            "lon":-0.0919983,
                            "country":"GB",
                            "state":"England"
                        },
                        {
                            "name":"London",
                            "local_names":{
                               "fr":"London",
                               "en":"London",
                               "ru":"Лондон"
                            },
                            "lat":42.9832406,
                            "lon":-81.243372,
                            "country":"CA",
                            "state":"Ontario"
                        },
                        {
                            "name":"Chelsea",
                            "local_names":{
                               "fr":"Chelsea",
                               "ru":"Челси",
                               "en":"Chelsea"
                            },
                            "lat":51.4875167,
                            "lon":-0.1687007,
                            "country":"GB",
                            "state":"England"
                        },
                        {
                            "name":"London",
                            "lat":37.1289771,
                            "lon":-84.0832646,
                            "country":"US",
                            "state":"Kentucky"
                        }
                    ]
        """;
        when(openWeatherApiService.getStringHttpResponse(anyString())).thenReturn(mockResponse);

        List<LocationSearchRes> locations = weatherService.findCities("London");

        assertAll(
                () -> assertEquals(5, locations.size()),
                () -> assertEquals("London", locations.getFirst().getName()),
                () -> assertEquals("England", locations.get(1).getState()),
                () -> assertEquals("CA", locations.get(2).getCountry()),
                () -> assertEquals("51.4875167", locations.get(3).getLat()),
                () -> assertEquals("-84.0832646", locations.get(4).getLon())
        );
    }

    @Test
    public void testAddLocation() {
        weatherService.addLocation("London", "GB", "51.5073219", "-0.1276474");

        Location savedLocation = locationRepository.findByLatitudeAndLongitudeAndUser("51.5073219", "-0.1276474", testUser);
        assertNotNull(savedLocation);
        assertEquals("London", savedLocation.getName());
    }

    @Test
    public void testFindTemperatures() throws URISyntaxException, IOException, InterruptedException {
        Location location = new Location("London", "GB", "51.5073219", "-0.1276474", testUser);

        String mockWeatherResponse = """
            {
                "coord": {
                   "lon": 7.367,
                   "lat": 45.133
                },
                "weather": [
                   {
                      "id": 501,
                      "main": "Rain",
                      "description": "red rain",
                      "icon": "10d"
                   }
                ],
                "base": "stations",
                "main": {
                   "temp": 284.2,
                   "feels_like": 282.93,
                   "temp_min": 283.06,
                   "temp_max": 286.82,
                   "pressure": 1021,
                   "humidity": 60,
                   "sea_level": 1021,
                   "grnd_level": 910
                },
                "visibility": 10000,
                "wind": {
                   "speed": 4.09,
                   "deg": 121,
                   "gust": 3.47
                },
                "rain": {
                   "1h": 2.73
                },
                "clouds": {
                   "all": 83
                },
                "dt": 1726660758,
                "sys": {
                   "type": 1,
                   "id": 6736,
                   "country": "IT",
                   "sunrise": 1726636384,
                   "sunset": 1726680975
                },
                "timezone": 7200,
                "id": 3165523,
                "name": "Province of Turin",
                "cod": 200
            }
        """;
        when(openWeatherApiService.getWeather(anyString(), anyString())).thenReturn(mockWeatherResponse);
        when(locationRepository.findByUser(null)).thenReturn(List.of(location));


        List<WeatherSearchRes> temperatures = weatherService.findTemperatures();

        assertAll(
                () -> assertEquals(1, temperatures.size()),
                () -> assertEquals("London", temperatures.getFirst().getName()),
                () -> assertEquals("Red rain", temperatures.getFirst().getWeather().getFirst().getDescription())
        );
    }

    @Test
    public void testDelLocation() {
        Location location = new Location("London", "GB", "51.5073219", "-0.1276474", testUser);
        locationRepository.save(location);

        weatherService.delLocation("51.5073219", "-0.1276474");

        Location deletedLocation = locationRepository.findByLatitudeAndLongitudeAndUser("51.5073219", "-0.1276474", testUser);
        assertNull(deletedLocation);
    }


    @TestConfiguration
    static class TestConfig {
        @Bean
        public OpenWeatherApiService openWeatherApiService() {
            return mock(OpenWeatherApiService.class);
        }
    }

}