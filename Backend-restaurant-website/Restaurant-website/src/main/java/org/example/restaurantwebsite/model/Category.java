package org.example.restaurantwebsite.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categories") // Убедитесь, что имя таблицы указано верно

public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Конструкторы
    public Category() {} // Пустой конструктор

    public Category(String name) {
        this.name = name;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
