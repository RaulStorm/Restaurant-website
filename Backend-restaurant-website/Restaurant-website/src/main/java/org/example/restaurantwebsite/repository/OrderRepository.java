package org.example.restaurantwebsite.repository;

import org.example.restaurantwebsite.model.Order;
import org.example.restaurantwebsite.model.OrderStatus;
import org.example.restaurantwebsite.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusOrderByIdAsc(OrderStatus status);
    List<Order> findByUser(User user);
}
