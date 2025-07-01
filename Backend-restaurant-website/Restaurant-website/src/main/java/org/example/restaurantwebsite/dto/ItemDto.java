package org.example.restaurantwebsite.dto;

public class ItemDto {
    private String name;
    private int quantity;

    public ItemDto() {}
    public ItemDto(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
