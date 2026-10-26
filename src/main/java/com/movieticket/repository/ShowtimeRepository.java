package com.movieticket.repository;

import com.movieticket.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByMovieIdAndShowDateAndStatus(Long movieId, LocalDate showDate, String status);

    @Query("SELECT s FROM Showtime s WHERE s.movie.id = :movieId AND LOWER(s.screen.cinema.city) = LOWER(:city) AND s.showDate = :showDate AND s.status = 'ACTIVE' ORDER BY s.startTime")
    List<Showtime> findByMovieCityAndDate(@Param("movieId") Long movieId, @Param("city") String city,
                                          @Param("showDate") LocalDate showDate);

    @Query("SELECT DISTINCT s.showDate FROM Showtime s WHERE s.movie.id = :movieId AND s.status = 'ACTIVE' AND s.showDate >= CURRENT_DATE ORDER BY s.showDate")
    List<LocalDate> findDistinctDatesForMovie(@Param("movieId") Long movieId);
}
