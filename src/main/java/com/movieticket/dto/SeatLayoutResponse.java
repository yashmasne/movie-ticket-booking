package com.movieticket.dto;

import com.movieticket.entity.Showtime;
import java.util.List;

public class SeatLayoutResponse {
    private Showtime showtime;
    private List<SeatDTO> seats;
    private int totalSeats;
    private int availableSeats;
    private int bookedSeats;

    public SeatLayoutResponse() {}

    public SeatLayoutResponse(Showtime showtime, List<SeatDTO> seats, int totalSeats, int availableSeats, int bookedSeats) {
        this.showtime = showtime;
        this.seats = seats;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.bookedSeats = bookedSeats;
    }

    public Showtime getShowtime() {
        return showtime;
    }

    public void setShowtime(Showtime showtime) {
        this.showtime = showtime;
    }

    public List<SeatDTO> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatDTO> seats) {
        this.seats = seats;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public int getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(int bookedSeats) {
        this.bookedSeats = bookedSeats;
    }
}
