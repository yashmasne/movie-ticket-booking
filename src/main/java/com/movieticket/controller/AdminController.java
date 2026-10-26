package com.movieticket.controller;

import com.movieticket.dto.ApiResponse;
import com.movieticket.entity.Booking;
import com.movieticket.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final BookingService bookingService;

    public AdminController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getDashboardStats() {
        Map<String, Object> stats = bookingService.getAdminStatistics();
        return ResponseEntity.ok(ApiResponse.ok("Admin stats retrieved", stats));
    }

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(ApiResponse.ok("All bookings retrieved", bookings));
    }
}
