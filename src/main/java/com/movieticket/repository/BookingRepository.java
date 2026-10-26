package com.movieticket.repository;

import com.movieticket.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingReference(String bookingReference);
    List<Booking> findByUserIdOrderByBookingTimeDesc(Long userId);
    List<Booking> findByCustomerEmailOrderByBookingTimeDesc(String customerEmail);
    List<Booking> findAllByOrderByBookingTimeDesc();
    long countByStatus(String status);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0.0) FROM Booking b WHERE b.status = 'CONFIRMED'")
    Double calculateTotalRevenue();
}
