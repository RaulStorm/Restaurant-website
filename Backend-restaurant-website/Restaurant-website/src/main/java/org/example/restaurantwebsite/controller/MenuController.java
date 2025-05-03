package org.example.restaurantwebsite.controller;

import org.example.restaurantwebsite.model.MenuItem;
import org.example.restaurantwebsite.dto.MenuItemDto;
import org.example.restaurantwebsite.model.MenuItemImage;
import org.example.restaurantwebsite.repository.MenuItemRepository;
import org.example.restaurantwebsite.service.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class MenuController {

    private final MenuItemRepository menuItemRepository;
    private final CloudinaryService cloudinaryService;

    // Внедрение зависимостей
    public MenuController(MenuItemRepository menuItemRepository, CloudinaryService cloudinaryService) {
        this.menuItemRepository = menuItemRepository;
        this.cloudinaryService = cloudinaryService;
    }

    // Получение всех меню
    @GetMapping("/menu")
    public ResponseEntity<List<MenuItemDto>> getMenu() {
        List<MenuItem> menuItems = menuItemRepository.findAll();
        if (menuItems.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<MenuItemDto> dtos = menuItems.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // Преобразование MenuItem в DTO
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

    // Получение одного элемента меню
    @GetMapping("/menu/{id}")
    public ResponseEntity<MenuItem> getMenuItem(@PathVariable Long id) {
        return menuItemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Загрузка изображения для элемента меню
    @PostMapping("/menu/{id}/upload-image")
    public ResponseEntity<String> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            // Загружаем изображение в Cloudinary
            String imageUrl = cloudinaryService.uploadImage(file);

            // Получаем меню по ID
            MenuItem menuItem = menuItemRepository.findById(id).orElseThrow(() -> new Exception());

            // Создаем объект изображения и связываем с блюдом
            MenuItemImage menuItemImage = new MenuItemImage();
            menuItemImage.setMenuItemId(menuItem.getId());
            menuItemImage.setImageUrl(imageUrl);  // URL изображения из Cloudinary
            menuItem.getImages().add(menuItemImage);  // Добавляем в список изображений

            // Сохраняем меню с обновленными изображениями
            menuItemRepository.save(menuItem);

            return ResponseEntity.ok("Изображение успешно загружено");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Ошибка загрузки изображения");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Меню не найдено");
        }
    }
}
