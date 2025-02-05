package org.example.weatherapp.repository;

import org.example.weatherapp.model.Location;
import org.example.weatherapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    List<Location> findByUser(User user);
    void deleteByLatitudeAndLongitudeAndUser(String latitude, String longitude, User user);
    boolean existsByLatitudeAndLongitudeAndUser(String latitude, String longitude, User user);
}
