package org.example.restaurantwebsite.controller;

import org.example.restaurantwebsite.model.Response;
import org.example.restaurantwebsite.model.UserDto;
import org.example.restaurantwebsite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Response register(@RequestBody UserDto userDto) {
        String token = userService.registerUser(userDto.getName(), userDto.getEmail(), userDto.getPassword());
        return token != null ? new Response(true, token) : new Response(false, "User already exists");
    }

    @PostMapping("/login")
    public Response login(@RequestBody UserDto userDto) {
        String token = userService.authenticate(userDto.getEmail(), userDto.getPassword());
        return token != null ? new Response(true, token) : new Response(false, "Invalid credentials");
    }
}
