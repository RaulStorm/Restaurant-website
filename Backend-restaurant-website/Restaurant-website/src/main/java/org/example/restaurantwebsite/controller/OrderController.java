package org.example.restaurantwebsite.controller;

import org.example.restaurantwebsite.model.*;
import org.example.restaurantwebsite.repository.MenuItemRepository;
import org.example.restaurantwebsite.repository.OrderRepository;
import org.example.restaurantwebsite.repository.UserRepository;
import org.example.restaurantwebsite.repository.ReservationRepository;
import org.example.restaurantwebsite.model.OrderItemRequest;
import org.example.restaurantwebsite.security.JwtTokenUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public OrderController(OrderRepository orderRepository,
                           MenuItemRepository menuItemRepository,
                           UserRepository userRepository,
                           ReservationRepository reservationRepository,
                           JwtTokenUtil jwtTokenUtil) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(token);

        // Получаем пользователя по email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Получаем заказ (если он связан с резервацией)
        Reservation reservation = null;
        if (orderRequest.getReservationId() != null) {
            reservation = reservationRepository.findById(orderRequest.getReservationId())
                    .orElseThrow(() -> new RuntimeException("Reservation not found"));
        }

        // Создаем новый заказ
        Order order = new Order();
        order.setUser(user);
        order.setTableNumber(orderRequest.getTableNumber());
        order.setOrderNotes(orderRequest.getOrderNotes());
        order.setReservation(reservation);  // Связываем заказ с резервированием (если есть)

        // Добавление элементов в заказ
        for (OrderItemRequest itemRequest : orderRequest.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItem(menuItem);  // Связываем с блюдом
            orderItem.setQuantity(itemRequest.getQuantity());  // Количество
            orderItem.setOrder(order);  // Связываем с заказом

            // Добавляем элемент в заказ
            order.getOrderItems().add(orderItem);  // Теперь список OrderItems не пустой
        }

        // Сохраняем заказ в базе данных
        Order savedOrder = orderRepository.save(order);

        // Возвращаем успешный ответ с сохраненным заказом
        return ResponseEntity.ok(savedOrder);
    }
}
