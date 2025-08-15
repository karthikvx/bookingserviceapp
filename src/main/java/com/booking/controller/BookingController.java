package com.booking.controller;

import com.booking.dto.BookingRequest;
import com.booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @GetMapping
    public String listBookings(Model model) {
        model.addAttribute("bookings", bookingService.getAllActiveBookings());
        model.addAttribute("bookingRequest", new BookingRequest());
        return "bookings/list";
    }

    @PostMapping("/add")
    public String addBooking(@Valid @ModelAttribute BookingRequest bookingRequest,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("bookings", bookingService.getAllActiveBookings());
            return "bookings/list";
        }
        try {
            bookingService.addBooking(bookingRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Booking added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/bookings";
    }

    @PostMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(id);
            redirectAttributes.addFlashAttribute("successMessage", "Booking cancelled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/bookings";
    }

    @GetMapping("/user/{userId}")
    public String getUserBookings(@PathVariable String userId, Model model) {
        model.addAttribute("bookings", bookingService.getUserBookings(userId));
        model.addAttribute("userId", userId);
        return "bookings/user-bookings";
    }
}