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

    // Метод для бронирования столика
    public Optional<String> reserveTable(Reservation reservation) {
        // Проверка на доступность столика
        Optional<Reservation> existingReservation = reservationRepository.findByTableIdAndReservationTime(
                reservation.getTable().getId(), reservation.getReservationTime());

        if (existingReservation.isPresent()) {
            return Optional.of("Столик уже забронирован на это время. Пожалуйста, выберите другое время.");
        }

        // Проверка, существует ли столик
        if (!tableRepository.existsById(reservation.getTable().getId())) {
            return Optional.of("Столик не существует.");
        }

        // Сохраняем бронирование
        reservationRepository.save(reservation);
        return Optional.empty();
    }
}
