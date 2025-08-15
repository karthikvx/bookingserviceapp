package com.booking.controller;

import com.booking.dto.BookingResponse;
import com.booking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingRestController.class)
class BookingRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    void addBooking_returnsCreated() throws Exception {
        BookingResponse response = new BookingResponse(1L, "user1", "res1", LocalDateTime.now(), "ACTIVE", LocalDateTime.now());
        response.setMessage("Booking created successfully");
        Mockito.when(bookingService.addBooking(any())).thenReturn(response);

        String json = """
            {
                "userId": "user1",
                "resourceId": "res1",
                "bookingDate": "2024-06-01T10:00:00"
            }
            """;

        mockMvc.perform(post("/api/bookings/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("user1"));
    }

    @Test
    void getAllActiveBookings_returnsList() throws Exception {
        BookingResponse response = new BookingResponse(1L, "user1", "res1", LocalDateTime.now(), "ACTIVE", LocalDateTime.now());
        Mockito.when(bookingService.getAllActiveBookings()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/bookings/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("user1"));
    }
}