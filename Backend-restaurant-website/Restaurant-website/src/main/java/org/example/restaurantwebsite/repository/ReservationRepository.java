package org.example.restaurantwebsite.repository;

import org.example.restaurantwebsite.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByTableIdAndReservationTime(Long tableId, Date reservationTime);

    // Можно добавить дополнительные методы для поиска по статусу и времени
}
