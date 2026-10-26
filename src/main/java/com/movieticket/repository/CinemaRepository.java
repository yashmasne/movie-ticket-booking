package com.movieticket.repository;

import com.movieticket.entity.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CinemaRepository extends JpaRepository<Cinema, Long> {
    @Query("SELECT DISTINCT c.city FROM Cinema c ORDER BY c.city")
    List<String> findAllCities();
}
