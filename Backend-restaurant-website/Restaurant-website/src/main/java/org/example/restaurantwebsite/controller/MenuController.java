package org.example.restaurantwebsite.controller;// MenuController.java

import org.example.restaurantwebsite.model.MenuItemDto;
import org.example.restaurantwebsite.model.MenuItem;
import org.example.restaurantwebsite.model.MenuItemImage;
import org.example.restaurantwebsite.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class MenuController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @GetMapping("/menu")
    public ResponseEntity<List<MenuItemDto>> getMenu() {
        List<MenuItem> menuItems = menuItemRepository.findAllWithImages();
        if (menuItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No menu items found");
        }
        List<MenuItemDto> dtos = menuItems.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    private MenuItemDto convertToDto(MenuItem menuItem) {
        MenuItemDto dto = new MenuItemDto();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setCategoryName(menuItem.getCategory().getName());
        dto.setPrice(menuItem.getPrice());
        dto.setImages(menuItem.getImages().stream().map(MenuItemImage::getImageUrl).collect(Collectors.toList()));
        return dto;
    }
}
