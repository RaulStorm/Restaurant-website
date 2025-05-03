package org.example.restaurantwebsite.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
public class MenuItemDto {

    private Long id;
    private String name;
    private String description;
    private String categoryName;
    private Double price;
    private List<String> images;


}
