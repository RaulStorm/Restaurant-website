package org.example.restaurantwebsite.repository;

import org.example.restaurantwebsite.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
