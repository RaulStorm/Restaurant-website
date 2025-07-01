package org.example.restaurantwebsite.controller;

import lombok.RequiredArgsConstructor;
import org.example.restaurantwebsite.model.OrderRequest;
import org.example.restaurantwebsite.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest request, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Пользователь не авторизован");
        }
        String username = authentication.getName();
        orderService.createOrder(request, username);
        // Возвращаем сообщение в виде JSON-строки:
        return ResponseEntity.ok("{\"message\":\"Заказ оформлен успешно\"}");
    }

}
