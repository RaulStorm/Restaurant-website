package org.example.restaurantwebsite.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RestaurantTableDto {
    private Long id;
    private String tableNumber;
}
