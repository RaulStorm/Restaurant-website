package org.example.restaurantwebsite.model;

import lombok.Getter;
import lombok.Setter;
import org.example.restaurantwebsite.model.OrderItemRequest;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private String tableNumber;      // Номер столика
    private String orderNotes;       // Примечания к заказу
    private Long reservationId;      // ID резервации (опционально)
    private List<OrderItemRequest> items; // Элементы заказа

}

