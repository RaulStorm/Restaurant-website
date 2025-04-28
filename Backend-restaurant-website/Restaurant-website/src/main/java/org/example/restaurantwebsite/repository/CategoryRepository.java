package org.example.restaurantwebsite.repository;

import org.example.restaurantwebsite.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Метод для поиска категории по имени
    Category findByName(String name);
}
