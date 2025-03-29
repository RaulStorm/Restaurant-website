package org.example.restaurantwebsite.controller;

import org.example.restaurantwebsite.model.Reservation;
import org.example.restaurantwebsite.service.ReservationService;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/api/reservations")
    public Reservation makeReservation(@RequestBody Reservation reservation) {
        return reservationService.saveReservation(reservation);
    }
}
