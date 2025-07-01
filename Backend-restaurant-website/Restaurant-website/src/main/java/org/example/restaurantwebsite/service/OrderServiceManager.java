package org.example.restaurantwebsite.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.restaurantwebsite.model.Order;
import org.example.restaurantwebsite.model.OrderStatus;
import org.example.restaurantwebsite.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

// OrderService.java
@Service
public class OrderServiceManager {
    private final OrderRepository repo;

    public OrderServiceManager(OrderRepository repo) {
        this.repo = repo;
    }

    /** Все активные заказы в порядке возрастания ID */
    public List<Order> getActiveOrders() {
        return repo.findByStatusOrderByIdAsc(OrderStatus.ACTIVE);
    }

    /** Пометка заказа как выполненного */
    @Transactional
    public void completeOrder(Long orderId) {
        Order o = repo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        o.setStatus(OrderStatus.COMPLETED);
        // так как статус полeм наблюдается в контексте, он будет сохранён автоматически
    }
}
