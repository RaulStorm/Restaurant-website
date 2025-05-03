package org.example.restaurantwebsite.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "restaurant_tables") // Название таблицы в БД
public class RestaurantTable {
    // Геттеры и сеттеры
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String tableNumber; // Номер столика
    @Setter
    private int seats; // Количество мест


}
