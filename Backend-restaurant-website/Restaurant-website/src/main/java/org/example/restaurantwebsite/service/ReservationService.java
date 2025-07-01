package org.example.restaurantwebsite.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.restaurantwebsite.model.Reservation;
import org.example.restaurantwebsite.repository.ReservationRepository;
import org.example.restaurantwebsite.repository.RestaurantTableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date; // для работы с SQL
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

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
        if (reservation.getTable() == null || reservation.getReservationTime() == null) {
            return Optional.of("Invalid data for reservation.");
        }

        Long tableId = reservation.getTable().getId();
        java.util.Date startTime = reservation.getReservationTime();
        java.util.Date endTime = reservation.getReservationEndTime();

        // Если время окончания не установлено, вычисляем его как startTime + 3 часа
        if (endTime == null) {
            endTime = new java.util.Date(startTime.getTime() + 3 * 60 * 60 * 1000);
        }

        // Преобразуем java.util.Date в java.sql.Date для использования в запросах
        Date sqlStartTime = new Date(startTime.getTime());
        Date sqlEndTime = new Date(endTime.getTime());

        // Проверка на пересечение бронирований для заданного столика и времени
        if (isTableReservedWithinTimeRange(tableId, sqlStartTime, sqlEndTime)) {
            return Optional.of("The table is already reserved at this time. Please choose another time.");
        }

        // Проверка, существует ли стол
        if (!tableRepository.existsById(tableId)) {
            return Optional.of("Table not found.");
        }

        // Сохраняем бронирование
        reservationRepository.save(reservation);
        return Optional.empty();
    }

    private boolean isTableReservedWithinTimeRange(Long tableId, Date startTime, Date endTime) {
        Optional<Reservation> existingReservation = reservationRepository
                .findOverlappingReservations(tableId, startTime, endTime);
        return existingReservation.isPresent();
    }

    @Transactional
    public void createReservation(Reservation reservation) {
        Optional<String> error = reserveTable(reservation);
        if (error.isPresent()) {
            throw new IllegalArgumentException(error.get());
        }
    }

    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));

        reservationRepository.delete(reservation);
    }

}
