package org.example.restaurantwebsite.repository;

import org.example.restaurantwebsite.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
