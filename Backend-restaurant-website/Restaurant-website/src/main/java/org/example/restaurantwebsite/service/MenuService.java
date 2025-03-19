package org.example.restaurantwebsite.service;

import org.example.restaurantwebsite.model.MenuItem;
import org.example.restaurantwebsite.repository.MenuItemRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    public List<MenuItem> getAllMenuItems() {
        Iterable<MenuItem> iterable = menuItemRepository.findAllWithImages(); // Убедитесь, что этот метод существует

        // Преобразуем Iterable в List
        List<MenuItem> list = StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());

        return list;
    }
}
