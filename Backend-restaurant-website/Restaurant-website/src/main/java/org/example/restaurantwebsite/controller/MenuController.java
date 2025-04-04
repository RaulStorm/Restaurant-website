package org.example.restaurantwebsite.controller;

import org.example.restaurantwebsite.model.MenuItemDto;
import org.example.restaurantwebsite.model.MenuItem;
import org.example.restaurantwebsite.model.MenuItemImage;
import org.example.restaurantwebsite.repository.MenuItemRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.CrossOrigin;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class MenuController {

    private final MenuItemRepository menuItemRepository;

    public MenuController(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

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
        return getMenuItemDto(menuItem);
    }

    public static MenuItemDto getMenuItemDto(MenuItem menuItem) {
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
