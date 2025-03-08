package org.example.restaurantwebsite.repository;

import org.example.restaurantwebsite.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}
