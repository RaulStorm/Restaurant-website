package org.example.restaurantwebsite.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tableNumber;      // Номер столика
    private String orderNotes;       // Примечания к заказу

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Связь с OrderItem: cascade = CascadeType.ALL, чтобы все элементы заказов сохранялись вместе с заказом
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();  // Инициализация списка

    @Column(name = "created_at", updatable = false)
    private java.sql.Timestamp createdAt;  // Время создания заказа

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = true)  // Связь с резервацией столика (может быть NULL)
    private Reservation reservation;  // Заказ может быть связан с резервированием столика
}
