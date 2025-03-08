package org.example.restaurantwebsite.controller;

import org.example.restaurantwebsite.model.Reservation;
import org.example.restaurantwebsite.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @PostMapping("/api/reservations")
    public Reservation makeReservation(@RequestBody Reservation reservation) {
        return reservationService.saveReservation(reservation);
    }
}
