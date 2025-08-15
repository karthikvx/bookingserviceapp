package com.booking.repository;

import com.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.userId = :userId AND b.resourceId = :resourceId " +
            "AND b.bookingDate = :bookingDate AND b.status = 'ACTIVE'")
    Optional<Booking> findActiveBooking(@Param("userId") String userId,
                                        @Param("resourceId") String resourceId,
                                        @Param("bookingDate") LocalDateTime bookingDate);

    List<Booking> findByUserIdAndStatus(String userId, String status);
    List<Booking> findByResourceIdAndStatus(String resourceId, String status);

    @Query("SELECT b FROM Booking b WHERE b.status = 'ACTIVE' ORDER BY b.bookingDate DESC")
    List<Booking> findAllActiveBookings();
}