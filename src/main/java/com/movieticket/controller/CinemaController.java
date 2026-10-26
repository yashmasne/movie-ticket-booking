package com.movieticket.controller;

import com.movieticket.dto.ApiResponse;
import com.movieticket.entity.Cinema;
import com.movieticket.entity.Screen;
import com.movieticket.entity.Seat;
import com.movieticket.repository.SeatRepository;
import com.movieticket.service.CinemaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/cinemas")
@CrossOrigin(origins = "*")
public class CinemaController {

    private final CinemaService cinemaService;
    private final SeatRepository seatRepository;

    public CinemaController(CinemaService cinemaService, SeatRepository seatRepository) {
        this.cinemaService = cinemaService;
        this.seatRepository = seatRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllCinemas() {
        return ResponseEntity.ok(ApiResponse.ok("Cinemas retrieved", cinemaService.getAllCinemas()));
    }

    @GetMapping("/cities")
    public ResponseEntity<ApiResponse> getCities() {
        return ResponseEntity.ok(ApiResponse.ok("Cities retrieved", cinemaService.getAllCities()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCinemaById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Cinema retrieved", cinemaService.getCinemaById(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/screens")
    public ResponseEntity<ApiResponse> getScreens(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Screens retrieved", cinemaService.getScreensByCinema(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createCinema(@RequestBody Cinema cinema) {
        try {
            Cinema created = cinemaService.createCinema(cinema);
            return ResponseEntity.ok(ApiResponse.ok("Cinema created", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{cinemaId}/screens")
    public ResponseEntity<ApiResponse> createScreen(@PathVariable Long cinemaId, @RequestBody Screen screen) {
        try {
            Screen created = cinemaService.createScreen(cinemaId, screen);
            // Automatically generate seats for screen
            int rows = created.getTotalRows() != null ? created.getTotalRows() : 6;
            int cols = created.getTotalCols() != null ? created.getTotalCols() : 8;
            List<Seat> seats = new ArrayList<>();
            for (int r = 0; r < rows; r++) {
                char rowChar = (char) ('A' + r);
                String rowStr = String.valueOf(rowChar);
                String tier = (r >= rows - 2) ? "VIP" : (r >= rows - 4 ? "PREMIUM" : "STANDARD");
                double multiplier = tier.equals("VIP") ? 1.8 : (tier.equals("PREMIUM") ? 1.35 : 1.0);

                for (int c = 1; c <= cols; c++) {
                    String code = rowStr + c;
                    seats.add(new Seat(created, rowStr, c, code, tier, multiplier));
                }
            }
            seatRepository.saveAll(seats);
            return ResponseEntity.ok(ApiResponse.ok("Screen and seat layout created", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
