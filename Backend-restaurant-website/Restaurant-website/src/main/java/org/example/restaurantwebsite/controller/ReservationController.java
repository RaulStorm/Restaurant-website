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
    public ResponseEntity<?> createReservation(@RequestBody ReservationDto reservationDto, HttpServletRequest request) {
        // Извлекаем токен из заголовка Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);

        // Получаем email (subject) из токена
        String email = jwtTokenUtil.getUsernameFromToken(token);

        // Ищем пользователя по email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        // Формируем объект бронирования
        Reservation reservation = new Reservation();
        reservation.setUser(user);

        // Преобразуем строку времени в объект Date
        Date reservationTime = parseReservationTime(reservationDto.getReservationTime());
        reservation.setReservationTime(reservationTime);
        reservation.setNumberOfPeople(reservationDto.getNumberOfPeople());

        // Получаем столик из БД через репозиторий
        Long tableId = reservationDto.getTable().getId();
        RestaurantTable restaurantTable = restaurantTableRepository.findById(tableId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Table not found"));
        reservation.setRestaurantTable(restaurantTable);

        // Сохраняем бронирование через сервис
        reservationService.createReservation(reservation);

        return ResponseEntity.ok("Reservation successful");
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
}
