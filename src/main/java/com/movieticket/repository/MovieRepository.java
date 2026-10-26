package com.movieticket.repository;

import com.movieticket.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByStatus(String status);

    @Query("SELECT m FROM Movie m WHERE " +
           "(:query IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(m.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(m.castMembers) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND (:genre IS NULL OR :genre = '' OR LOWER(m.genre) LIKE LOWER(CONCAT('%', LOWER(:genre), '%'))) " +
           "AND (:language IS NULL OR :language = '' OR LOWER(m.language) = LOWER(:language)) " +
           "AND (:status IS NULL OR :status = '' OR LOWER(m.status) = LOWER(:status))")
    List<Movie> searchMovies(@Param("query") String query, @Param("genre") String genre,
                             @Param("language") String language, @Param("status") String status);
}
