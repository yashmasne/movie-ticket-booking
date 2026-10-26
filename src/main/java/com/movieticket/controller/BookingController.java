package com.movieticket.controller;

import com.movieticket.dto.ApiResponse;
import com.movieticket.dto.BookingRequest;
import com.movieticket.entity.Booking;
import com.movieticket.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> bookTickets(@Valid @RequestBody BookingRequest req) {
        try {
            Booking booking = bookingService.bookTickets(req);
            return ResponseEntity.ok(ApiResponse.ok("Booking confirmed successfully!", booking));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to complete booking: " + e.getMessage()));
        }
    }

    @GetMapping("/{reference}")
    public ResponseEntity<ApiResponse> getBookingByReference(@PathVariable String reference) {
        try {
            Booking booking = bookingService.getBookingByReference(reference);
            return ResponseEntity.ok(ApiResponse.ok("Booking found", booking));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getBookingsByUser(@PathVariable Long userId) {
        List<Booking> bookings = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.ok("User bookings retrieved", bookings));
    }

    @GetMapping("/email")
    public ResponseEntity<ApiResponse> getBookingsByEmail(@RequestParam String email) {
        List<Booking> bookings = bookingService.getBookingsByEmail(email);
        return ResponseEntity.ok(ApiResponse.ok("Bookings retrieved for " + email, bookings));
    }

    @PostMapping("/{reference}/cancel")
    public ResponseEntity<ApiResponse> cancelBooking(@PathVariable String reference) {
        try {
            Booking cancelled = bookingService.cancelBooking(reference);
            return ResponseEntity.ok(ApiResponse.ok("Booking cancelled and seats released. Refund simulated to original payment method.", cancelled));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to cancel booking: " + e.getMessage()));
        }
    }
}
