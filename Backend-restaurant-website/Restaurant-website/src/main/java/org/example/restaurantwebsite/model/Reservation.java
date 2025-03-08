package org.example.restaurantwebsite.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(name = "number_of_people")
    private int numberOfPeople;
    @Column(name = "reservation_time")
    private String reservationTime;

    // Getters and Setters
    // ...
}
