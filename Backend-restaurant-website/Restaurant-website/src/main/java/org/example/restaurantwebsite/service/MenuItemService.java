package org.example.restaurantwebsite.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.restaurantwebsite.model.MenuItem;
import org.example.restaurantwebsite.model.MenuItemImage;
import org.example.restaurantwebsite.repository.MenuItemRepository;
import org.example.restaurantwebsite.repository.MenuItemImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuItemImageRepository menuItemImageRepository;

    // Внедрение зависимостей
    public MenuItemService(MenuItemRepository menuItemRepository, MenuItemImageRepository menuItemImageRepository) {
        this.menuItemRepository = menuItemRepository;
        this.menuItemImageRepository = menuItemImageRepository;
    }

    // Добавление блюда
    public MenuItem addMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    // Добавление изображения для блюда
    public void addImage(MenuItemImage menuItemImage) {
        menuItemImageRepository.save(menuItemImage);
    }


    public List<MenuItem> findAll() {
        return menuItemRepository.findAll();
    }

    // Метод для удаления по ID
    public void deleteById(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Блюдо с ID='" + id + "' не найдено"));
        menuItemRepository.delete(item);
    }
}
