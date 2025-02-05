package org.example.weatherapp.repository;

import org.example.weatherapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String login);
}
