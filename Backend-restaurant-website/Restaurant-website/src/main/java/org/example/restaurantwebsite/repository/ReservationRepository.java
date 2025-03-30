package org.example.restaurantwebsite.repository;

import org.example.restaurantwebsite.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.table.id = :tableId " +
            "AND (:startTime < r.reservationEndTime AND :endTime > r.reservationTime)")
    Optional<Reservation> findByTableIdAndTimeOverlap(
            @Param("tableId") Long tableId,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime
    );
}
