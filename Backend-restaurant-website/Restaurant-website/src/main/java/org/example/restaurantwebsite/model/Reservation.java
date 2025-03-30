package org.example.restaurantwebsite.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
@Getter
@Entity
public class Reservation {
    // Геттеры и сеттеры
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private RestaurantTable table; // Связь со столиком

    @Temporal(TemporalType.TIMESTAMP)
    private Date reservationTime; // Время начала брони

    @Temporal(TemporalType.TIMESTAMP)
    private Date reservationEndTime; // Время окончания брони

    @PrePersist
    public void setReservationEndTime() {
        this.reservationEndTime = new Date(this.reservationTime.getTime() + 3 * 60 * 60 * 1000);
    }

    public void setTable(RestaurantTable table) { this.table = table; }

    public void setReservationTime(Date reservationTime) {
        this.reservationTime = reservationTime;
        this.reservationEndTime = new Date(this.reservationTime.getTime() + 3 * 60 * 60 * 1000);
    }
}
