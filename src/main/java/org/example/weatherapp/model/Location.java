package org.example.weatherapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String country;

    @JsonProperty("lat")
    @Column(nullable = false)
    private String latitude;

    @JsonProperty("lon")
    @Column(nullable = false)
    private String longitude;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Location(String name, String country, String latitude, String longitude, User user) {
        this.name = name;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.user = user;
    }
}

