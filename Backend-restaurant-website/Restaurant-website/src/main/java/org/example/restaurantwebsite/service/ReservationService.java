package org.example.restaurantwebsite.service;

import org.example.restaurantwebsite.model.Reservation;
import org.example.restaurantwebsite.repository.ReservationRepository;
import org.example.restaurantwebsite.repository.RestaurantTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestaurantTableRepository tableRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, RestaurantTableRepository tableRepository) {
        this.reservationRepository = reservationRepository;
        this.tableRepository = tableRepository;
    }

    public Optional<String> reserveTable(Reservation reservation) {
        if (reservation.getTable() == null || reservation.getReservationTime() == null) {
            return Optional.of("Некорректные данные для бронирования.");
        }

        Long tableId = reservation.getTable().getId();
        Date startTime = reservation.getReservationTime();
        Date endTime = reservation.getReservationEndTime(); // Теперь это поле в БД

        // Проверяем пересечение бронирований
        if (reservationRepository.findByTableIdAndTimeOverlap(tableId, startTime, endTime).isPresent()) {
            return Optional.of("Столик уже забронирован в это время. Выберите другое время.");
        }

        if (!tableRepository.existsById(tableId)) {
            return Optional.of("Столик не существует.");
        }

        reservationRepository.save(reservation);
        return Optional.empty();
    }
}
