package org.example.restaurantwebsite.telegram;

import org.example.restaurantwebsite.dto.MenuItemDto;
import org.example.restaurantwebsite.dto.ReservationWithIdDto;
import org.example.restaurantwebsite.dto.UserDto;
import org.example.restaurantwebsite.model.*;
import org.example.restaurantwebsite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class AuthService {

    private final RestTemplate restTemplate;
    private final UserService userService;

    @Autowired
    public AuthService(RestTemplate restTemplate, UserService userService) {
        this.restTemplate = restTemplate;
        this.userService = userService;
    }

    public String login(String email, String password) {
        String token = userService.authenticate(email, password);
        if (token != null) {
            return token;
        }
        return null;
    }

    public String register(String name, String email, String password) {
        String token = userService.registerUser(name, email, password);
        return token;
    }

    public UserDto getUserInfo(String token) {
        // Извлекаем email из токена
        String email = userService.extractEmailFromToken(token);
        if (email == null) {
            return null;
        }

        User user = userService.findByEmail(email).orElse(null);
        if (user != null) {
            UserDto userDto = new UserDto();
            userDto.setName(user.getName());
            userDto.setEmail(user.getEmail());
            return userDto;
        }
        return null;
    }

    public List<ReservationWithIdDto> getUserReservations(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            ResponseEntity<ReservationWithIdDto[]> response = restTemplate.exchange(
                    "http://localhost:8080/api/profile/reservations",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    ReservationWithIdDto[].class
            );

            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public ResponseEntity<String> cancelReservation(String token, Long reservationId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            return restTemplate.exchange(
                    "http://localhost:8080/api/profile/reservations/" + reservationId,
                    HttpMethod.DELETE,
                    new HttpEntity<>(headers),
                    String.class
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при отмене бронирования");
        }
    }
    public ReviewResponse getUserLastReview(String token) {
        try {
            ResponseEntity<ReviewResponse> response = restTemplate.exchange(
                    "http://localhost:8080/api/profile/user-review",
                    HttpMethod.GET,
                    new HttpEntity<>(createHeaders(token)),
                    ReviewResponse.class
            );
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private HttpHeaders createHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public List<OrderResponse> getUserOrders(String token) {
        try {
            ResponseEntity<OrderResponse[]> response = restTemplate.exchange(
                    "http://localhost:8080/api/profile/orders",
                    HttpMethod.GET,
                    new HttpEntity<>(createHeaders(token)),
                    OrderResponse[].class
            );
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<MenuItemDto> getFavoriteDishes(String token) {
        try {
            ResponseEntity<MenuItemDto[]> response = restTemplate.exchange(
                    "http://localhost:8080/api/profile/favorite-dishes",
                    HttpMethod.GET,
                    new HttpEntity<>(createHeaders(token)),
                    MenuItemDto[].class
            );
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}