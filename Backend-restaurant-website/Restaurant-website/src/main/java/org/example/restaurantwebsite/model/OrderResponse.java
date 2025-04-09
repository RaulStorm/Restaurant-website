package org.example.restaurantwebsite.model;

import lombok.Data;

@Data
public class OrderResponse {
    private Long id;
    private String status; // Предполагается, что в Order есть поле status
    // Добавьте другие поля по необходимости
}
