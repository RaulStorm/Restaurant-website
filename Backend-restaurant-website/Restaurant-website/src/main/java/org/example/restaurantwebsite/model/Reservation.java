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

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private RestaurantTable table;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonProperty("numberOfPeople")
    @Column(name = "number_of_people", nullable = false)
    private Integer numberOfPeople;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reservation_time", nullable = false)
    private Date reservationTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reservation_end_time", nullable = true)
    private Date reservationEndTime;

    
    @Column(name = "name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PrePersist
    public void calculateReservationEndTime() {
        if (this.reservationTime != null) {
            this.reservationEndTime = new Date(this.reservationTime.getTime() + 3 * 60 * 60 * 1000); // 3 hours
        }
    }

    public void setRestaurantTable(RestaurantTable restaurantTable) {
        this.table = restaurantTable;
    }

    public RestaurantTable getRestaurantTable() {
        return this.table;
    }

    // Метод для установки пользователя
    public void setUser(User user) {
        this.user = user;
    }

    // Метод для установки количества людей
    public void setNumberOfPeople(Integer numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }
}