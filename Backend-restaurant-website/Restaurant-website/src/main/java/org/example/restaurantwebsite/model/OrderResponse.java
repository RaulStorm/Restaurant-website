package org.example.restaurantwebsite.model;

import org.example.restaurantwebsite.dto.ItemDto;

import java.util.List;

public class OrderResponse {
    private Long id;
    private List<ItemDto> items;

    // геттеры/сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<ItemDto> getItems() { return items; }
    public void setItems(List<ItemDto> items) { this.items = items; }
}
