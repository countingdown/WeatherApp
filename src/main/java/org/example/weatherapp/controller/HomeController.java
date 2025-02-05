package org.example.weatherapp.controller;

import lombok.RequiredArgsConstructor;
import org.example.weatherapp.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.net.URISyntaxException;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final WeatherService weatherService;

    @GetMapping("/index")
    public String home(Model model) throws URISyntaxException, IOException, InterruptedException {
        model.addAttribute("temperatures", weatherService.findTemperatures());
        return "index";
    }

}
