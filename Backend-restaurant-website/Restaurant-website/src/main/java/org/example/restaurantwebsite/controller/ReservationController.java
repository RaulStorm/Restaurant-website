package org.example.restaurantwebsite.controller;

import org.example.restaurantwebsite.model.Reservation;
import org.example.restaurantwebsite.model.Response;
import org.example.restaurantwebsite.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5500")
@RestController
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // Эндпоинт для бронирования столика
    @PostMapping("/reserve")
    public ResponseEntity<?> reserveTable(@RequestBody Reservation reservation) {
        Optional<String> result = reservationService.reserveTable(reservation);

        if (result.isPresent()) {
            return ResponseEntity.badRequest().body(new Response(false, result.get()));
        } else {
            return ResponseEntity.ok(new Response(true, "Столик успешно забронирован"));
        }
    }
}
