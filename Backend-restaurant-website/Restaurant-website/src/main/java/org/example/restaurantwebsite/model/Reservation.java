package org.example.restaurantwebsite.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Table;
import java.util.Date;


@Setter
@Getter
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private RestaurantTable table;

    @Column(name = "reservation_time", nullable = false)
    private Date reservationTime;

    @Column(name = "number_of_people", nullable = false)
    private int numberOfPeople;

    @Column(name = "name", nullable = true)
    private String name;  // Это имя пользователя, если необходимо

    // Getters and Setters

}
