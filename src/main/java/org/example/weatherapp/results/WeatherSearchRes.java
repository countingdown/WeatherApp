package org.example.weatherapp.results;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherSearchRes {
    @JsonProperty("weather")
    private List<Weather> weather;

    @JsonProperty("main")
    private Main main;

    private String name;

    private String country;

    private String lat;

    private String lon;

    @Data
    public static class Weather {
        private String icon;

        private String description;
    }

    @Data
    public static class Main {
        private String temp;

        private String feels_like;

        private String humidity;
    }
}
