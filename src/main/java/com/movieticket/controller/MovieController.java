package com.movieticket.controller;

import com.movieticket.dto.ApiResponse;
import com.movieticket.entity.Movie;
import com.movieticket.service.MovieService;
import com.movieticket.service.ShowtimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MovieController {

    private final MovieService movieService;
    private final ShowtimeService showtimeService;

    public MovieController(MovieService movieService, ShowtimeService showtimeService) {
        this.movieService = movieService;
        this.showtimeService = showtimeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getMovies(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String status) {
        List<Movie> movies = movieService.searchMovies(search, genre, language, status);
        return ResponseEntity.ok(ApiResponse.ok("Movies retrieved", movies));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getMovieById(@PathVariable Long id) {
        try {
            Movie movie = movieService.getMovieById(id);
            return ResponseEntity.ok(ApiResponse.ok("Movie retrieved", movie));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/dates")
    public ResponseEntity<ApiResponse> getMovieDates(@PathVariable Long id) {
        List<LocalDate> dates = showtimeService.getAvailableDatesForMovie(id);
        return ResponseEntity.ok(ApiResponse.ok("Show dates retrieved", dates));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createMovie(@RequestBody Movie movie) {
        try {
            Movie saved = movieService.createMovie(movie);
            return ResponseEntity.ok(ApiResponse.ok("Movie created successfully", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create movie: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateMovie(@PathVariable Long id, @RequestBody Movie movie) {
        try {
            Movie updated = movieService.updateMovie(id, movie);
            return ResponseEntity.ok(ApiResponse.ok("Movie updated successfully", updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update movie: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteMovie(@PathVariable Long id) {
        try {
            movieService.deleteMovie(id);
            return ResponseEntity.ok(ApiResponse.ok("Movie deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete movie: " + e.getMessage()));
        }
    }
}
