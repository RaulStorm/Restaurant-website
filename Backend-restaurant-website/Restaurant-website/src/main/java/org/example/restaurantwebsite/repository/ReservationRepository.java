package org.example.restaurantwebsite.repository;

import org.example.restaurantwebsite.model.Reservation;
import org.example.restaurantwebsite.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Метод для поиска пересекающихся бронирований для заданного столика и времени
    @Query("SELECT r FROM Reservation r WHERE r.table.id = :tableId " +
            "AND ((r.reservationTime BETWEEN :startTime AND :endTime) OR " +
            "(r.reservationEndTime BETWEEN :startTime AND :endTime))")
    Optional<Reservation> findOverlappingReservations(@Param("tableId") Long tableId,
                                                      @Param("startTime") Date startTime,
                                                      @Param("endTime") Date endTime);
    List<Reservation> findByUser(User user);

    List<Reservation> findByReservationTimeBetween(Timestamp start, Timestamp end);
}
