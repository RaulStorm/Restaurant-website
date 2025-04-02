package org.example.restaurantwebsite.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequest {
    private Long menuItemId;         // ID блюда
    private int quantity;            // Количество
}