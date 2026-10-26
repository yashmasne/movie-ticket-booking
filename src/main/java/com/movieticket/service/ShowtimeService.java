package com.movieticket.service;

import com.movieticket.dto.SeatDTO;
import com.movieticket.dto.SeatLayoutResponse;
import com.movieticket.dto.ShowtimeRequest;
import com.movieticket.entity.Movie;
import com.movieticket.entity.Screen;
import com.movieticket.entity.Seat;
import com.movieticket.entity.Showtime;
import com.movieticket.repository.BookingSeatRepository;
import com.movieticket.repository.MovieRepository;
import com.movieticket.repository.ScreenRepository;
import com.movieticket.repository.SeatRepository;
import com.movieticket.repository.ShowtimeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;
    private final BookingSeatRepository bookingSeatRepository;

    public ShowtimeService(ShowtimeRepository showtimeRepository,
                           MovieRepository movieRepository,
                           ScreenRepository screenRepository,
                           SeatRepository seatRepository,
                           BookingSeatRepository bookingSeatRepository) {
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
        this.screenRepository = screenRepository;
        this.seatRepository = seatRepository;
        this.bookingSeatRepository = bookingSeatRepository;
    }

    public List<Showtime> getAllShowtimes() {
        return showtimeRepository.findAll();
    }

    public List<Showtime> getShowtimesByMovieAndDate(Long movieId, LocalDate date) {
        return showtimeRepository.findByMovieIdAndShowDateAndStatus(movieId, date, "ACTIVE");
    }

    public List<Showtime> getShowtimesByMovieCityDate(Long movieId, String city, LocalDate date) {
        if (city == null || city.trim().isEmpty() || city.equalsIgnoreCase("All")) {
            return showtimeRepository.findByMovieIdAndShowDateAndStatus(movieId, date, "ACTIVE");
        }
        return showtimeRepository.findByMovieCityAndDate(movieId, city, date);
    }

    public List<LocalDate> getAvailableDatesForMovie(Long movieId) {
        return showtimeRepository.findDistinctDatesForMovie(movieId);
    }

    public Showtime getShowtimeById(Long id) {
        return showtimeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Showtime not found with ID: " + id));
    }

    public SeatLayoutResponse getSeatLayout(Long showtimeId) {
        Showtime showtime = getShowtimeById(showtimeId);
        Long screenId = showtime.getScreen().getId();
        List<Seat> screenSeats = seatRepository.findByScreenIdOrderBySeatRowAscSeatNumberAsc(screenId);

        List<Long> bookedSeatIdsList = bookingSeatRepository.findBookedSeatIdsForShowtime(showtimeId);
        Set<Long> bookedSeatIds = new HashSet<>(bookedSeatIdsList);

        List<SeatDTO> seatDTOs = new ArrayList<>();
        int bookedCount = 0;

        for (Seat seat : screenSeats) {
            boolean isBooked = bookedSeatIds.contains(seat.getId());
            if (isBooked) {
                bookedCount++;
            }
            double calculatedPrice = Math.round(showtime.getBasePrice() * seat.getPriceMultiplier());
            seatDTOs.add(new SeatDTO(
                    seat.getId(),
                    seat.getSeatRow(),
                    seat.getSeatNumber(),
                    seat.getSeatCode(),
                    seat.getTier(),
                    seat.getPriceMultiplier(),
                    calculatedPrice,
                    isBooked
            ));
        }

        int totalSeats = screenSeats.size();
        int availableSeats = totalSeats - bookedCount;

        return new SeatLayoutResponse(showtime, seatDTOs, totalSeats, availableSeats, bookedCount);
    }

    public Showtime createShowtime(ShowtimeRequest req) {
        Movie movie = movieRepository.findById(req.getMovieId())
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + req.getMovieId()));
        Screen screen = screenRepository.findById(req.getScreenId())
                .orElseThrow(() -> new IllegalArgumentException("Screen not found with ID: " + req.getScreenId()));

        LocalTime endTime = req.getEndTime();
        if (endTime == null && movie.getDurationMinutes() != null) {
            endTime = req.getStartTime().plusMinutes(movie.getDurationMinutes() + 20); // 20m interval
        }

        Showtime showtime = new Showtime(
                movie,
                screen,
                req.getShowDate(),
                req.getStartTime(),
                endTime,
                req.getBasePrice(),
                "ACTIVE"
        );
        return showtimeRepository.save(showtime);
    }

    public void deleteShowtime(Long id) {
        if (!showtimeRepository.existsById(id)) {
            throw new IllegalArgumentException("Showtime not found with ID: " + id);
        }
        showtimeRepository.deleteById(id);
    }
}
