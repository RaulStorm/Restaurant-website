package org.example.restaurantwebsite.service;

import org.example.restaurantwebsite.model.Reservation;
import org.example.restaurantwebsite.repository.ReservationRepository;
import org.example.restaurantwebsite.repository.RestaurantTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Optional<String> reserveTable(Reservation reservation) {
        // Проверяем наличие необходимых данных
        if (reservation.getRestaurantTable() == null || reservation.getReservationTime() == null) {
            return Optional.of("Некорректные данные для бронирования.");
        }

        Long tableId = reservation.getRestaurantTable().getId();
        Date startTime = reservation.getReservationTime();
        Date endTime = reservation.getReservationEndTime(); // если это поле используется

        // Проверяем, не пересекается ли новое бронирование с уже существующими
        if (reservationRepository.findByTableIdAndTimeOverlap(tableId, startTime, endTime).isPresent()) {
            return Optional.of("Столик уже забронирован в это время. Выберите другое время.");
        }

        // Проверяем, существует ли стол
        if (!tableRepository.existsById(tableId)) {
            return Optional.of("Столик не существует.");
        }

        // Сохраняем бронь
        reservationRepository.save(reservation);
        return Optional.empty();
    }

    @Transactional
    public void createReservation(Reservation reservation) {
        Optional<String> error = reserveTable(reservation);
        if (error.isPresent()) {
            // Здесь можно выбросить исключение, либо обработать ошибку и вернуть сообщение
            throw new IllegalArgumentException(error.get());
        }
    }
}
