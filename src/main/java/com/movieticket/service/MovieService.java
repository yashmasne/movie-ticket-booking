package com.movieticket.service;

import com.movieticket.entity.Movie;
import com.movieticket.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public List<Movie> getMoviesByStatus(String status) {
        return movieRepository.findByStatus(status);
    }

    public List<Movie> searchMovies(String query, String genre, String language, String status) {
        return movieRepository.searchMovies(
                (query != null && !query.trim().isEmpty()) ? query.trim() : null,
                genre,
                language,
                status
        );
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + id));
    }

    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public Movie updateMovie(Long id, Movie movieDetails) {
        Movie movie = getMovieById(id);
        movie.setTitle(movieDetails.getTitle());
        movie.setDescription(movieDetails.getDescription());
        movie.setGenre(movieDetails.getGenre());
        movie.setLanguage(movieDetails.getLanguage());
        movie.setDurationMinutes(movieDetails.getDurationMinutes());
        movie.setRating(movieDetails.getRating());
        movie.setVotes(movieDetails.getVotes());
        movie.setReleaseDate(movieDetails.getReleaseDate());
        movie.setPosterUrl(movieDetails.getPosterUrl());
        movie.setBannerUrl(movieDetails.getBannerUrl());
        movie.setTrailerUrl(movieDetails.getTrailerUrl());
        movie.setDirector(movieDetails.getDirector());
        movie.setCastMembers(movieDetails.getCastMembers());
        movie.setAgeRating(movieDetails.getAgeRating());
        movie.setStatus(movieDetails.getStatus());
        return movieRepository.save(movie);
    }

    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new IllegalArgumentException("Movie not found with ID: " + id);
        }
        movieRepository.deleteById(id);
    }
}
