package org.example.restaurantwebsite.repository;

import org.example.restaurantwebsite.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
