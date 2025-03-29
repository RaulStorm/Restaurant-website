package org.example.restaurantwebsite.service;

import org.example.restaurantwebsite.model.MenuItem;
import org.example.restaurantwebsite.repository.MenuItemRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Service;

@Service
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    public MenuService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItem> getAllMenuItems() {
        Iterable<MenuItem> iterable = menuItemRepository.findAllWithImages(); // Убедитесь, что этот метод существует


        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }
}
