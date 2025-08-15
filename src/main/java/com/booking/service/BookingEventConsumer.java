package com.booking.service;

import com.booking.dto.BookingResponse;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BookingEventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(BookingEventConsumer.class);

    @KafkaListener(topics = "booking-events", groupId = "booking-group")
    public void handleBookingEvent(BookingResponse bookingResponse) {
        logger.info("Received booking event: {}", bookingResponse);
        switch (bookingResponse.getStatus()) {
            case "ACTIVE":
                logger.info("Processing new booking: {}", bookingResponse.getId());
                break;
            case "CANCELLED":
                logger.info("Processing cancelled booking: {}", bookingResponse.getId());
                break;
        }
    }
}