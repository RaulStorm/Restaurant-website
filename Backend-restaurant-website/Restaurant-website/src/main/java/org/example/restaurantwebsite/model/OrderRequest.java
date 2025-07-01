package org.example.restaurantwebsite.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private String tableNumber;
    private String orderNotes;
    private List<OrderItemRequest> items;
}
