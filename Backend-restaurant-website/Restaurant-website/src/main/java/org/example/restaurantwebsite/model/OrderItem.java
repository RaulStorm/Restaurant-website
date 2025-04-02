package org.example.restaurantwebsite.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "menu_item_id", nullable = false)  // Убедись, что поле не null
    private MenuItem menuItem;  // Связь с конкретным блюдом

    private int quantity;       // Количество

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)  // Связь с заказом
    private Order order;
}
