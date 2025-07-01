package org.example.restaurantwebsite.dto;

import java.math.BigDecimal;

public record WaiterOrderItemView(
        String itemName,
        int quantity,
        Double unitPrice
) {}
