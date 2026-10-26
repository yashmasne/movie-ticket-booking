package com.movieticket.service;

import com.movieticket.entity.Cinema;
import com.movieticket.entity.Screen;
import com.movieticket.repository.CinemaRepository;
import com.movieticket.repository.ScreenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CinemaService {

    private final CinemaRepository cinemaRepository;
    private final ScreenRepository screenRepository;

    public CinemaService(CinemaRepository cinemaRepository, ScreenRepository screenRepository) {
        this.cinemaRepository = cinemaRepository;
        this.screenRepository = screenRepository;
    }

    public List<Cinema> getAllCinemas() {
        return cinemaRepository.findAll();
    }

    public List<String> getAllCities() {
        return cinemaRepository.findAllCities();
    }

    public Cinema getCinemaById(Long id) {
        return cinemaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cinema not found with ID: " + id));
    }

    public Cinema createCinema(Cinema cinema) {
        return cinemaRepository.save(cinema);
    }

    public Screen createScreen(Long cinemaId, Screen screen) {
        Cinema cinema = getCinemaById(cinemaId);
        screen.setCinema(cinema);
        return screenRepository.save(screen);
    }

    public List<Screen> getScreensByCinema(Long cinemaId) {
        return screenRepository.findByCinemaId(cinemaId);
    }
}
