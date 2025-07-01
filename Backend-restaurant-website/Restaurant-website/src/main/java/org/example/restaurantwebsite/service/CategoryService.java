package org.example.restaurantwebsite.service;

import org.example.restaurantwebsite.model.Category;
import org.example.restaurantwebsite.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // Внедрение зависимостей
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Метод для поиска категории по имени
    public Category getCategoryByName(String categoryName) {
        return categoryRepository.findByName(categoryName);
    }
}
