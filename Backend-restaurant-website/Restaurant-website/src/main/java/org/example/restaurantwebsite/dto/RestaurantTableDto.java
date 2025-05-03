package org.example.restaurantwebsite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
public class RestaurantTableDto {
    private Long id;
    private String tableNumber;


}
