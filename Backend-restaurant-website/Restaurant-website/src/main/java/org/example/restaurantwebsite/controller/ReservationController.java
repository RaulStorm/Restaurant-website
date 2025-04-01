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
        // Извлекаем токен
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(createErrorResponse("Missing or invalid Authorization header"));
        }
        String token = authHeader.substring(7);

        // Получаем email пользователя
        String email = jwtTokenUtil.getUsernameFromToken(token);

        // Получаем пользователя
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        // Преобразуем дату из строки
        Date reservationTime = parseReservationTime(reservationDto.getReservationTime());

        // 🔴 1. Проверка: нельзя бронировать в прошлом
        if (reservationTime.before(new Date())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("You cannot reserve a table in the past."));
        }

        // Получаем столик
        Long tableId = reservationDto.getTable().getId();
        RestaurantTable restaurantTable = restaurantTableRepository.findById(tableId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Table not found"));

        // 🔴 2. Проверка: нельзя бронировать на большее число человек, чем вмещает стол
        int requestedPeople = reservationDto.getNumberOfPeople();
        if (requestedPeople > restaurantTable.getSeats()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("The number of people exceeds the number of seats at this table."));
        }

        // Формируем и заполняем объект Reservation
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setReservationTime(new java.sql.Date(reservationTime.getTime()));
        reservation.setNumberOfPeople(requestedPeople);
        reservation.setRestaurantTable(restaurantTable);
        reservation.setName(reservationDto.getName());

        // Сохраняем бронирование
        reservationService.createReservation(reservation);

        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(createSuccessResponse("Table successfully reserved"));
    }


    private Date parseReservationTime(String reservationTimeStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        try {
            return sdf.parse(reservationTimeStr);
        } catch (ParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid reservation time format");
        }
    }

    private Map<String, String> createErrorResponse(String errorMessage) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", errorMessage);
        return errorResponse;
    }

    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("message", message);
        return successResponse;
    }
}
