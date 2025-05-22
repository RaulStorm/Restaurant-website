package org.example.restaurantwebsite.dto;

import java.time.LocalDateTime;
import java.util.List;

public record WaiterOrderView(
        Long orderId,
        String tableNumber,
        String orderNotes,
        LocalDateTime orderTime,
        String clientName,
        List<WaiterOrderItemView> items
) {}
