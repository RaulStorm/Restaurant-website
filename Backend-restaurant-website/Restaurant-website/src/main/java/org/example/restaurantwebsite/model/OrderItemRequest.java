package org.example.restaurantwebsite.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequest {
    private Long menuItemId;
    private int quantity;
}
