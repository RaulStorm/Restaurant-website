package org.example.restaurantwebsite.controller;

import lombok.RequiredArgsConstructor;
import org.example.restaurantwebsite.model.*;
import org.example.restaurantwebsite.repository.MenuItemRepository;
import org.example.restaurantwebsite.repository.OrderRepository;
import org.example.restaurantwebsite.repository.UserRepository;
import org.example.restaurantwebsite.repository.ReservationRepository;
import org.example.restaurantwebsite.model.OrderItemRequest;
import org.example.restaurantwebsite.security.JwtTokenUtil;
import org.example.restaurantwebsite.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest orderRequest, Authentication auth) {
        orderService.createOrder(orderRequest, auth.getName());
        return ResponseEntity.ok("{\"message\": \"Заказ успешно создан\"}");
    }
}
