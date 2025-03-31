package org.example.restaurantwebsite.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Связь со столиком
    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private RestaurantTable table;

    // Связь с пользователем
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonProperty("numberOfPeople")
    @Column(name = "number_of_people", nullable = false)
    private Integer numberOfPeople;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reservation_time", nullable = false)
    private Date reservationTime;

    // Для миграции схемы временно допускаем NULL, но это поле будет вычисляться автоматически
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reservation_end_time", nullable = true)
    private Date reservationEndTime;

    // При сохранении вычисляем время окончания бронирования (reservationTime + 3 часа)
    @PrePersist
    public void calculateReservationEndTime() {
        if (this.reservationTime != null) {
            this.reservationEndTime = new Date(this.reservationTime.getTime() + 3 * 60 * 60 * 1000);
        }
    }
    public void setRestaurantTable(RestaurantTable restaurantTable) {
        this.table = restaurantTable;
    }

    public RestaurantTable getRestaurantTable() {
        return this.table;
    }
}
