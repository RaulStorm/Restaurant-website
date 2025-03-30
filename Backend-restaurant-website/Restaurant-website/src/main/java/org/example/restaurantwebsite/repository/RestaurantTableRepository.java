package org.example.restaurantwebsite.repository;

import org.example.restaurantwebsite.model.RestaurantTable ;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable , Long> {
    // Дополнительные методы, если необходимо
}
