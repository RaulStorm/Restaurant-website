package org.example.restaurantwebsite.controller;

import org.example.restaurantwebsite.dto.WaiterOrderItemView;
import org.example.restaurantwebsite.dto.WaiterOrderView;
import org.example.restaurantwebsite.model.Order;
import org.example.restaurantwebsite.service.OrderServiceManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/waiter")
@PreAuthorize("hasRole('WAITER')")
public class WaiterController {

    private final OrderServiceManager orderServiceManager;

    public WaiterController(OrderServiceManager orderServiceManager) {
        this.orderServiceManager = orderServiceManager;
    }

    @GetMapping("/orders/active")
    public List<WaiterOrderView> listActive() {
        return orderServiceManager.getActiveOrders().stream()
                .map(this::toView)
                .toList();
    }

    @PostMapping("/orders/{id}/complete")
    public ResponseEntity<Void> complete(@PathVariable Long id) {
        orderServiceManager.completeOrder(id);
        return ResponseEntity.noContent().build();
    }

    private WaiterOrderView toView(Order o) {
        var itemsViews = o.getItems().stream()
                .map(it -> new WaiterOrderItemView(
                        it.getMenuItem().getName(),
                        it.getQuantity(),
                        it.getMenuItem().getPrice()
                ))
                .toList();

        return new WaiterOrderView(
                o.getId(),
                o.getTableNumber(),
                o.getOrderNotes(),
                o.getOrderTime(),
                o.getUser().getName(),
                itemsViews
        );
    }
}
