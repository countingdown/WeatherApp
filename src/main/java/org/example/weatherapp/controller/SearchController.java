package org.example.weatherapp.controller;

import lombok.RequiredArgsConstructor;
import org.example.weatherapp.service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.net.URISyntaxException;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final WeatherService weatherService;

    @GetMapping("/find")
    public String findCity(@RequestParam String city, Model model) throws URISyntaxException, IOException, InterruptedException {
        model.addAttribute("cities", weatherService.findCities(city));
        model.addAttribute("searching", city);
        return "search-results";
    }

    @PostMapping("/add")
    public String addLocation(@RequestParam String name,
                              @RequestParam String country,
                              @RequestParam String lat,
                              @RequestParam String lon,
                              Model model) throws URISyntaxException, IOException, InterruptedException {
        weatherService.addLocation(name, country, lat, lon);
        model.addAttribute("temperatures", weatherService.findTemperatures());
        return "redirect:index";
    }

    @PostMapping("/delete")
    public String deleteLocation(@RequestParam String latitude,
                                 @RequestParam String longitude,
                              Model model) throws URISyntaxException, IOException, InterruptedException {
        weatherService.delLocation(latitude, longitude);
        model.addAttribute("temperatures", weatherService.findTemperatures());
        return "redirect:index";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgumentException(IllegalArgumentException e) {
        ModelAndView modelAndView = new ModelAndView("argException"); // Укажите имя вашего представления
        modelAndView.addObject("searching", e.getMessage());
        modelAndView.addObject("status", HttpStatus.BAD_REQUEST.value());
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        return modelAndView;
    }

}
