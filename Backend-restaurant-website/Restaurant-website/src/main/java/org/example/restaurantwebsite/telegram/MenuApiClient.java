package org.example.restaurantwebsite.telegram;

import org.example.restaurantwebsite.dto.MenuItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class MenuApiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String MENU_API_URL = "http://localhost:8080/api/menu";

    private List<MenuItemDto> cachedMenu = new ArrayList<>();
    private final Map<String, List<MenuItemDto>> categorizedCache = new HashMap<>();

    public List<MenuItemDto> fetchAllMenuItems() {
        ResponseEntity<MenuItemDto[]> response = restTemplate.getForEntity(MENU_API_URL, MenuItemDto[].class);
        MenuItemDto[] menuItems = response.getBody();
        if (menuItems != null) {
            cachedMenu = Arrays.asList(menuItems);
            categorizedCache.clear();
            for (MenuItemDto item : cachedMenu) {
                String category = item.getCategoryName().trim().toLowerCase();
                categorizedCache.computeIfAbsent(category, k -> new ArrayList<>()).add(item);
            }
        }
        return cachedMenu;
    }

    public List<MenuItemDto> fetchMenuByCategory(String category) {
        if (cachedMenu.isEmpty()) {
            fetchAllMenuItems();
        }
        String key = category.trim().toLowerCase();
        return categorizedCache.getOrDefault(key, Collections.emptyList());
    }

    public List<String> fetchCategories() {
        if (cachedMenu.isEmpty()) {
            fetchAllMenuItems();
        }
        return cachedMenu.stream()
                .map(item -> item.getCategoryName().trim())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // Новый метод для получения всех блюд по категориям
    public Map<String, List<MenuItemDto>> fetchAllMenuItemsGroupedByCategory() {
        if (cachedMenu.isEmpty()) {
            fetchAllMenuItems();
        }
        return categorizedCache;
    }
}
