package org.example.restaurantwebsite.telegram;

import org.example.restaurantwebsite.model.MenuItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class MenuApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public List<MenuItemDto> fetchMenuByCategory(String categoryName) {
        try {
            ResponseEntity<MenuItemDto[]> response = restTemplate.getForEntity("http://localhost:8080/api/menu", MenuItemDto[].class);
            MenuItemDto[] allItems = response.getBody();
            if (allItems == null) return Collections.emptyList();
            return Arrays.stream(allItems)
                    .filter(item -> item.getCategoryName() != null && item.getCategoryName().equalsIgnoreCase(categoryName))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<String> fetchCategories() {
        try {
            ResponseEntity<MenuItemDto[]> response = restTemplate.getForEntity("http://localhost:8080/api/menu", MenuItemDto[].class);
            MenuItemDto[] allItems = response.getBody();
            if (allItems == null) return Collections.emptyList();
            return Arrays.stream(allItems)
                    .map(MenuItemDto::getCategoryName)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
