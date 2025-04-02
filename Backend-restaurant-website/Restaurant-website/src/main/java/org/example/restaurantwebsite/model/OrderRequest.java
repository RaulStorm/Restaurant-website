package org.example.restaurantwebsite.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.restaurantwebsite.model.OrderItemRequest;

import java.util.List;

@Data
public class OrderRequest {
    private String tableNumber;
    private String orderNotes;
    private List<OrderItemRequest> items;
}

