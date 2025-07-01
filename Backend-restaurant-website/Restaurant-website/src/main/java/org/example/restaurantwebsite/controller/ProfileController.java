package org.example.restaurantwebsite.controller;

import lombok.RequiredArgsConstructor;
import org.example.restaurantwebsite.dto.ItemDto;
import org.example.restaurantwebsite.dto.MenuItemDto;
import org.example.restaurantwebsite.dto.ReservationWithIdDto;
import org.example.restaurantwebsite.dto.RestaurantTableDto;
import org.example.restaurantwebsite.model.*;
import org.example.restaurantwebsite.repository.OrderRepository;
import org.example.restaurantwebsite.repository.ReservationRepository;
import org.example.restaurantwebsite.repository.ReviewRepository;
import org.example.restaurantwebsite.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getUserOrders(Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<Order> orders = orderRepository.findByUser(user);

        // сортируем по убыванию ID (или по дате, если она есть)
        orders.sort((o1, o2) -> Long.compare(o2.getId(), o1.getId()));

        // берём только 5 последних
        List<Order> latestOrders = orders.stream().limit(5).toList();

        List<OrderResponse> dtos = latestOrders.stream().map(order -> {
            OrderResponse dto = new OrderResponse();
            dto.setId(order.getId());

            List<ItemDto> items = order.getItems().stream()
                    .map(oi -> new ItemDto(oi.getMenuItem().getName(), oi.getQuantity()))
                    .collect(Collectors.toList());

            dto.setItems(items);
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }




    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationWithIdDto>> getReservations(Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<Reservation> reservations = reservationRepository.findByUser(user);

        Date now = new Date();
        List<ReservationWithIdDto> dtos = reservations.stream()
                .filter(r -> r.getReservationTime().after(now))
                .map(r -> {
                    ReservationWithIdDto dto = new ReservationWithIdDto();

                    SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm");

                    dto.setId(r.getId());
                    dto.setReservationTime(dfDate.format(r.getReservationTime()) + " " + dfTime.format(r.getReservationTime()));
                    dto.setNumberOfPeople(r.getNumberOfPeople());
                    dto.setName(r.getName());
                    dto.setTable(new RestaurantTableDto(r.getRestaurantTable().getId(), r.getRestaurantTable().getTableNumber()));
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }






    // Эндпоинт для получения списка любимых блюд пользователя
    @GetMapping("/favorite-dishes")
    public ResponseEntity<List<MenuItemDto>> getFavoriteDishes(Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Получаем все заказы пользователя
        List<Order> orders = orderRepository.findByUser(user);

        // Считываем все items, которые были в заказах
        Map<MenuItem, Integer> menuItemCountMap = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem orderItem : order.getItems()) {
                MenuItem menuItem = orderItem.getMenuItem();
                int quantity = orderItem.getQuantity();

                // Суммируем количество заказанных блюд
                menuItemCountMap.put(menuItem, menuItemCountMap.getOrDefault(menuItem, 0) + quantity);
            }
        }

        // Сортируем блюда по количеству заказов
        List<MenuItem> topMenuItems = menuItemCountMap.entrySet().stream()
                .sorted((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Преобразуем в MenuItemDto
        List<MenuItemDto> dtos = topMenuItems.stream().map(item -> {
            MenuItemDto dto = new MenuItemDto();
            dto.setId(item.getId());
            dto.setName(item.getName());
            dto.setDescription(item.getDescription());
            dto.setCategoryName(item.getCategory().getName());
            dto.setPrice(item.getPrice());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }




    @GetMapping("/user-review")
    public ResponseEntity<ReviewResponse> getUserReview(Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<Review> reviews = reviewRepository.findByUserOrderByCreatedAtDesc(user);

        if (reviews.isEmpty()) {
            return ResponseEntity.ok(new ReviewResponse(null, 0, null));
        }

        Review lastReview = reviews.get(0);

        return ResponseEntity.ok(new ReviewResponse(
                lastReview.getReviewText(),
                lastReview.getRating(),
                lastReview.getFormattedDate()
        ));
    }




    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<String> cancelReservation(@PathVariable Long reservationId, Authentication auth) {
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Бронь не найдена"));

        if (reservation.getUser() == null || !reservation.getUser().getEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Это не ваше бронирование.");
        }

        reservationRepository.delete(reservation);

        return ResponseEntity
                .ok()
                .header("Content-Type", "text/plain; charset=UTF-8")
                .body("Бронь успешно отменена.");
    }


}
