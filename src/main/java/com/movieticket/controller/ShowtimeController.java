package com.movieticket.controller;

import com.movieticket.dto.ApiResponse;
import com.movieticket.dto.SeatLayoutResponse;
import com.movieticket.dto.ShowtimeRequest;
import com.movieticket.entity.Showtime;
import com.movieticket.service.ShowtimeService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
@CrossOrigin(origins = "*")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    public ShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getShowtimes(
            @RequestParam(required = false) Long movieId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        if (movieId != null) {
            LocalDate targetDate = (date != null) ? date : LocalDate.now();
            List<Showtime> showtimes = showtimeService.getShowtimesByMovieCityDate(movieId, city, targetDate);
            return ResponseEntity.ok(ApiResponse.ok("Showtimes retrieved", showtimes));
        }

        return ResponseEntity.ok(ApiResponse.ok("All showtimes", showtimeService.getAllShowtimes()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getShowtimeById(@PathVariable Long id) {
        try {
            Showtime showtime = showtimeService.getShowtimeById(id);
            return ResponseEntity.ok(ApiResponse.ok("Showtime retrieved", showtime));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<ApiResponse> getSeatLayout(@PathVariable Long id) {
        try {
            SeatLayoutResponse layout = showtimeService.getSeatLayout(id);
            return ResponseEntity.ok(ApiResponse.ok("Seat layout retrieved", layout));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createShowtime(@Valid @RequestBody ShowtimeRequest req) {
        try {
            Showtime created = showtimeService.createShowtime(req);
            return ResponseEntity.ok(ApiResponse.ok("Showtime created successfully", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create showtime: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteShowtime(@PathVariable Long id) {
        try {
            showtimeService.deleteShowtime(id);
            return ResponseEntity.ok(ApiResponse.ok("Showtime deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete showtime: " + e.getMessage()));
        }
    }
}
