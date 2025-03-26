package org.example.restaurantwebsite.controller;

import org.example.restaurantwebsite.model.UserDto;
import org.example.restaurantwebsite.model.Response;
import org.example.restaurantwebsite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserDto userDto, BindingResult result) {
        logger.info("Пришёл запрос на регистрацию: {}", userDto);

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(new Response(false, result.getAllErrors().toString()));
        }

        try {
            String token = userService.registerUser(userDto.getName(), userDto.getEmail(), userDto.getPassword());
            if (token == null) {
                return ResponseEntity.badRequest().body(new Response(false, "Пользователь с таким email уже существует"));
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", true);
            responseBody.put("message", "Регистрация успешна");

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            logger.error("Ошибка при регистрации", e);
            return ResponseEntity.status(500).body(new Response(false, "Внутренняя ошибка сервера"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserDto userDto, BindingResult result) {
        logger.info("Пришёл запрос на вход: {}", userDto);

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(new Response(false, result.getAllErrors().toString()));
        }

        try {
            String token = userService.authenticate(userDto.getEmail(), userDto.getPassword());
            if (token != null) {
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("success", true);
                responseBody.put("message", "Вход выполнен");
                responseBody.put("token", token);

                return ResponseEntity.ok(responseBody);
            } else {
                return ResponseEntity.badRequest().body(new Response(false, "Неправильный email или пароль"));
            }
        } catch (Exception e) {
            logger.error("Ошибка при входе", e);
            return ResponseEntity.status(500).body(new Response(false, "Внутренняя ошибка сервера"));
        }
    }
}
