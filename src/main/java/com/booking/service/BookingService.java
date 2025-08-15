package com.booking.service;

import com.booking.dto.BookingRequest;
import com.booking.dto.BookingResponse;
import com.booking.entity.Booking;
import com.booking.exception.DuplicateBookingException;
import com.booking.exception.BookingNotFoundException;
import com.booking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    private static final String BOOKING_TOPIC = "booking-events";

    public BookingResponse addBooking(BookingRequest request) {
        Optional<Booking> existingBooking = bookingRepository.findActiveBooking(
                request.getUserId(),
                request.getResourceId(),
                request.getBookingDate()
        );
        if (existingBooking.isPresent()) {
            throw new DuplicateBookingException(
                    "Booking already exists for user " + request.getUserId() +
                            " and resource " + request.getResourceId() +
                            " at " + request.getBookingDate()
            );
        }
        Booking booking = new Booking(
                request.getUserId(),
                request.getResourceId(),
                request.getBookingDate(),
                "ACTIVE"
        );
        Booking savedBooking = bookingRepository.save(booking);
        BookingResponse response = convertToResponse(savedBooking);
        response.setMessage("Booking created successfully");
        kafkaTemplate.send(BOOKING_TOPIC, "booking.created", response);
        return response;
    }

    public BookingResponse cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));
        if (!"ACTIVE".equals(booking.getStatus())) {
            throw new IllegalStateException("Cannot cancel booking with status: " + booking.getStatus());
        }
        booking.setStatus("CANCELLED");
        Booking savedBooking = bookingRepository.save(booking);
        BookingResponse response = convertToResponse(savedBooking);
        response.setMessage("Booking cancelled successfully");
        kafkaTemplate.send(BOOKING_TOPIC, "booking.cancelled", response);
        return response;
    }

    public List<BookingResponse> getAllActiveBookings() {
        return bookingRepository.findAllActiveBookings()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getUserBookings(String userId) {
        return bookingRepository.findByUserIdAndStatus(userId, "ACTIVE")
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private BookingResponse convertToResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getUserId(),
                booking.getResourceId(),
                booking.getBookingDate(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }
}