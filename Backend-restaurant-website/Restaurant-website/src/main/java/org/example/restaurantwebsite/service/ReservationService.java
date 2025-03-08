package org.example.restaurantwebsite.service;

import org.example.restaurantwebsite.model.Reservation;
import org.example.restaurantwebsite.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }
}
