package org.example.weatherapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class OpenWeatherApiService {

    @Value("${open_weather.api.key}")
    private String apiKey;

    @Value("${open_weather.api.inf.url}")
    private String infUrl;

    @Value("${open_weather.api.geo.url}")
    private String geoUrl;


    public String getWeather(String latitude, String longitude) throws URISyntaxException, IOException, InterruptedException {
        String weatherUrl = String.format("%s?lat=%s&lon=%s&units=metric&appid=%s", infUrl, latitude, longitude, apiKey);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(weatherUrl))
                .GET()
                .build();

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        return response.body();
    }

    public String getStringHttpResponse(String city) throws URISyntaxException, IOException, InterruptedException {
        String cityGeoUrlEd = String.format("%s?q=%s&limit=5&appid=%s", geoUrl, city, apiKey);
        String cityGeoUrl = cityGeoUrlEd.replace(" ", "+");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(cityGeoUrl))
                .GET()
                .build();

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        return response.body();
    }
}
