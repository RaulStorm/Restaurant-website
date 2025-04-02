package org.example.restaurantwebsite.service;

import lombok.RequiredArgsConstructor;
import org.example.restaurantwebsite.model.*;
import org.example.restaurantwebsite.model.OrderItemRequest;
import org.example.restaurantwebsite.model.OrderRequest;
import org.example.restaurantwebsite.repository.MenuItemRepository;
import org.example.restaurantwebsite.repository.OrderRepository;
import org.example.restaurantwebsite.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createOrder(OrderRequest request, String username) {
        // Находим пользователя по username
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Создаём заказ
        Order order = new Order();
        order.setUser(user);
        order.setTableNumber(request.getTableNumber());
        order.setOrderNotes(request.getOrderNotes());

        // Добавляем позиции заказа
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemRequest itemRequest : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Блюдо не найдено: id = " + itemRequest.getMenuItemId()));

            OrderItem item = new OrderItem();
            item.setMenuItem(menuItem);
            item.setQuantity(itemRequest.getQuantity());
            item.setOrder(order); // Устанавливаем связь с заказом

            items.add(item);
        }

        orderRepository.save(order);
    }
}
