package org.example.restaurantwebsite.controller;

import lombok.RequiredArgsConstructor;
import org.example.restaurantwebsite.model.MenuItemDto;
import org.example.restaurantwebsite.model.OrderResponse;
import org.example.restaurantwebsite.model.ReservationDto;
import org.example.restaurantwebsite.model.RestaurantTableDto;
import org.example.restaurantwebsite.model.ReviewResponse;
import org.example.restaurantwebsite.model.Order;
import org.example.restaurantwebsite.model.Reservation;
import org.example.restaurantwebsite.model.User;
import org.example.restaurantwebsite.repository.OrderRepository;
import org.example.restaurantwebsite.repository.ReservationRepository;
import org.example.restaurantwebsite.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;

    // Эндпоинт для получения истории заказов пользователя
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getUserOrders(Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        List<Order> orders = orderRepository.findByUser(user);
        List<OrderResponse> dtos = orders.stream().map(order -> {
            OrderResponse dto = new OrderResponse();
            dto.setId(order.getId());// При необходимости добавьте другие поля
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Эндпоинт для получения бронирований пользователя
    @GetMapping("/reservations")
    public List<ReservationDto> getReservations(Authentication auth) {
        // нашёл User
        List<Reservation> reservations = reservationRepository.findByUser(user);
        return reservations.stream().map(r -> {
            ReservationDto dto = new ReservationDto();
            // Допустим, reservationTime это Date. Разбиваем на date/time
            SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm");

            dto.setDate(dfDate.format(r.getReservationTime()));
            dto.setTime(dfTime.format(r.getReservationTime()));
            dto.setTableNumber(r.getTable().getTableNumber());
            return dto;
        }).collect(Collectors.toList());
    }


    // Эндпоинт для получения списка любимых блюд пользователя
    // Если функциональность еще не реализована, можно вернуть пустой список
    @GetMapping("/favorite-dishes")
    public ResponseEntity<List<MenuItemDto>> getFavoriteDishes(Authentication auth) {
        // Здесь можно вернуть пустой список, так как у User нет связи с любимыми блюдами
        return ResponseEntity.ok(List.of());
    }

    // Эндпоинт для получения отзыва пользователя
    // Если отзыва пока нет, возвращаем объект с пустым текстом
    @GetMapping("/user-review")
    public ResponseEntity<ReviewResponse> getUserReview(Authentication auth) {
        // Пока возвращаем заглушку
        return ResponseEntity.ok(new ReviewResponse(null));
    }
}
