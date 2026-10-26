package com.movieticket.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String genre;

    @Column(length = 50)
    private String language = "English";

    private Integer durationMinutes;

    private Double rating = 8.5;

    private Integer votes = 1200;

    private LocalDate releaseDate;

    @Column(length = 500)
    private String posterUrl;

    @Column(length = 500)
    private String bannerUrl;

    @Column(length = 500)
    private String trailerUrl;

    @Column(length = 100)
    private String director;

    @Column(length = 255)
    private String castMembers;

    @Column(length = 20)
    private String ageRating = "UA 16+";

    @Column(length = 20)
    private String status = "NOW_SHOWING"; // NOW_SHOWING, COMING_SOON

    public Movie() {}

    public Movie(String title, String description, String genre, String language, Integer durationMinutes,
                 Double rating, Integer votes, LocalDate releaseDate, String posterUrl, String bannerUrl,
                 String trailerUrl, String director, String castMembers, String ageRating, String status) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.language = language;
        this.durationMinutes = durationMinutes;
        this.rating = rating;
        this.votes = votes;
        this.releaseDate = releaseDate;
        this.posterUrl = posterUrl;
        this.bannerUrl = bannerUrl;
        this.trailerUrl = trailerUrl;
        this.director = director;
        this.castMembers = castMembers;
        this.ageRating = ageRating;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getCastMembers() {
        return castMembers;
    }

    public void setCastMembers(String castMembers) {
        this.castMembers = castMembers;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
