package org.example.restaurantwebsite.model;

import jakarta.persistence.*;
import org.example.restaurantwebsite.repository.TableStatus;

@Entity

public class RestaurantTable  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int number;  // Номер столика
    private int capacity;  // Вместимость столика

    @Enumerated(EnumType.STRING)
    private TableStatus status;  // Статус столика (например, available или reserved)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public TableStatus getStatus() {
        return status;
    }

    public void setStatus(TableStatus status) {
        this.status = status;
    }

    // Getters and Setters
}
