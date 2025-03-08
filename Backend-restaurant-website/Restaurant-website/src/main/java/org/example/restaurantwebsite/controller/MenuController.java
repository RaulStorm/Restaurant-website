package org.example.restaurantwebsite.controller;

import org.example.restaurantwebsite.model.MenuItem;
import org.example.restaurantwebsite.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MenuController {
    @Autowired
    private MenuService menuService;

    @GetMapping("/api/menu")
    public List<MenuItem> getMenu() {
        return menuService.getAllMenuItems();
    }
}
