package org.example.weatherapp.results;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class LocationSearchRes {

        private String name;

        private String lat;

        private String lon;

        private String country;

        private String state;
}
