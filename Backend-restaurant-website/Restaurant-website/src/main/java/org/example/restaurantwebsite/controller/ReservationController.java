package org.example.restaurantwebsite.controller;

import org.example.restaurantwebsite.model.ReservationDto;
import org.example.restaurantwebsite.model.Reservation;
import org.example.restaurantwebsite.model.RestaurantTable;
import org.example.restaurantwebsite.model.User;
import org.example.restaurantwebsite.repository.RestaurantTableRepository;
import org.example.restaurantwebsite.repository.UserRepository;
import org.example.restaurantwebsite.service.ReservationService;
import org.example.restaurantwebsite.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantTableRepository restaurantTableRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/reserve")
    public ResponseEntity<Map<String, String>> createReservation(@RequestBody ReservationDto reservationDto, HttpServletRequest request) {
        // Извлекаем токен из заголовка Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(createErrorResponse("Missing or invalid Authorization header"));
        }
        String token = authHeader.substring(7);

        // Получаем email (subject) из токена
        String email = jwtTokenUtil.getUsernameFromToken(token);

        // Ищем пользователя по email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        // Формируем объект бронирования
        Reservation reservation = new Reservation();
        reservation.setUser(user);  // Устанавливаем пользователя

        // Преобразуем строку времени в объект Date
        Date reservationTime = parseReservationTime(reservationDto.getReservationTime());

        // Преобразуем reservationTime в java.sql.Date, если необходимо
        java.sql.Date sqlReservationTime = new java.sql.Date(reservationTime.getTime());  // Преобразуем в java.sql.Date

        reservation.setReservationTime(sqlReservationTime);
        reservation.setNumberOfPeople(reservationDto.getNumberOfPeople());  // Устанавливаем количество людей

        // Получаем столик из БД через репозиторий
        Long tableId = reservationDto.getTable().getId();
        RestaurantTable restaurantTable = restaurantTableRepository.findById(tableId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Table not found"));
        reservation.setRestaurantTable(restaurantTable);  // Устанавливаем столик

        // Сохраняем бронирование через сервис
        reservationService.createReservation(reservation);

        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(createSuccessResponse("Table successfully reserved"));
    }

    // Метод для преобразования строки времени в объект Date
    private Date parseReservationTime(String reservationTimeStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        try {
            return sdf.parse(reservationTimeStr);
        } catch (ParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid reservation time format");
        }
    }

    // Метод для создания ответа об ошибке
    private Map<String, String> createErrorResponse(String errorMessage) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", errorMessage);
        return errorResponse;
    }

    // Метод для создания успешного ответа
    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("message", message);
        return successResponse;
    }
}
