package org.example.restaurantwebsite.repository;

import org.example.restaurantwebsite.model.Reservation;
import org.example.restaurantwebsite.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.reservationTime < :endTime AND r.reservationEndTime > :startTime")
    List<Reservation> findConflictingReservations(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

}
