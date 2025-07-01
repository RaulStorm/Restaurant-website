package org.example.restaurantwebsite.repository;

import org.example.restaurantwebsite.model.MenuItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemImageRepository extends JpaRepository<MenuItemImage, Long> {
}
