package com.movieticket.service;

import com.movieticket.dto.BookingRequest;
import com.movieticket.entity.Booking;
import com.movieticket.entity.BookingSeat;
import com.movieticket.entity.Seat;
import com.movieticket.entity.Showtime;
import com.movieticket.entity.User;
import com.movieticket.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final CinemaRepository cinemaRepository;

    public BookingService(BookingRepository bookingRepository,
                          BookingSeatRepository bookingSeatRepository,
                          ShowtimeRepository showtimeRepository,
                          SeatRepository seatRepository,
                          UserRepository userRepository,
                          MovieRepository movieRepository,
                          CinemaRepository cinemaRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.cinemaRepository = cinemaRepository;
    }

    @Transactional
    public Booking bookTickets(BookingRequest req) {
        Showtime showtime = showtimeRepository.findById(req.getShowtimeId())
                .orElseThrow(() -> new IllegalArgumentException("Showtime not found: " + req.getShowtimeId()));

        if (!"ACTIVE".equalsIgnoreCase(showtime.getStatus())) {
            throw new IllegalStateException("This showtime is no longer active for booking");
        }

        List<Long> bookedSeatIds = bookingSeatRepository.findBookedSeatIdsForShowtime(showtime.getId());
        Set<Long> bookedSet = new HashSet<>(bookedSeatIds);

        for (Long seatId : req.getSeatIds()) {
            if (bookedSet.contains(seatId)) {
                throw new IllegalStateException("Seat ID " + seatId + " is already booked for this showtime");
            }
        }

        List<Seat> seats = seatRepository.findByIdIn(req.getSeatIds());
        if (seats.size() != req.getSeatIds().size()) {
            throw new IllegalArgumentException("One or more selected seat IDs are invalid");
        }

        double subtotal = 0.0;
        for (Seat seat : seats) {
            subtotal += Math.round(showtime.getBasePrice() * seat.getPriceMultiplier());
        }

        // 18% GST / convenience fee
        double taxAmount = Math.round(subtotal * 0.18 * 100.0) / 100.0;
        double totalAmount = Math.round((subtotal + taxAmount) * 100.0) / 100.0;

        // Generate reference MT-XXXXXX
        String ref = "MT-" + (100000 + new Random().nextInt(900000));
        while (bookingRepository.findByBookingReference(ref).isPresent()) {
            ref = "MT-" + (100000 + new Random().nextInt(900000));
        }

        User user = null;
        if (req.getUserId() != null) {
            user = userRepository.findById(req.getUserId()).orElse(null);
        } else if (req.getCustomerEmail() != null) {
            user = userRepository.findByEmail(req.getCustomerEmail().toLowerCase().trim()).orElse(null);
        }

        String paymentId = req.getPaymentId();
        if (paymentId == null || paymentId.trim().isEmpty()) {
            paymentId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        Booking booking = new Booking(
                ref,
                user,
                req.getCustomerName(),
                req.getCustomerEmail(),
                req.getCustomerPhone(),
                showtime,
                seats.size(),
                subtotal,
                taxAmount,
                totalAmount,
                "CONFIRMED",
                req.getPaymentMethod() != null ? req.getPaymentMethod() : "CREDIT_CARD",
                paymentId
        );

        Booking savedBooking = bookingRepository.save(booking);

        List<BookingSeat> bookingSeats = new ArrayList<>();
        for (Seat seat : seats) {
            double seatPrice = Math.round(showtime.getBasePrice() * seat.getPriceMultiplier());
            BookingSeat bs = new BookingSeat(savedBooking, seat, seatPrice);
            bookingSeats.add(bookingSeatRepository.save(bs));
        }

        savedBooking.setBookingSeats(bookingSeats);
        return savedBooking;
    }

    public Booking getBookingByReference(String reference) {
        return bookingRepository.findByBookingReference(reference)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + reference));
    }

    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserIdOrderByBookingTimeDesc(userId);
    }

    public List<Booking> getBookingsByEmail(String email) {
        return bookingRepository.findByCustomerEmailOrderByBookingTimeDesc(email.toLowerCase().trim());
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAllByOrderByBookingTimeDesc();
    }

    @Transactional
    public Booking cancelBooking(String reference) {
        Booking booking = getBookingByReference(reference);
        if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        booking.setStatus("CANCELLED");
        return bookingRepository.save(booking);
    }

    public Map<String, Object> getAdminStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMovies", movieRepository.count());
        stats.put("totalCinemas", cinemaRepository.count());
        stats.put("totalShowtimes", showtimeRepository.count());
        stats.put("totalBookings", bookingRepository.count());
        stats.put("confirmedBookings", bookingRepository.countByStatus("CONFIRMED"));
        stats.put("cancelledBookings", bookingRepository.countByStatus("CANCELLED"));
        Double totalRev = bookingRepository.calculateTotalRevenue();
        stats.put("totalRevenue", totalRev != null ? totalRev : 0.0);
        return stats;
    }
}
