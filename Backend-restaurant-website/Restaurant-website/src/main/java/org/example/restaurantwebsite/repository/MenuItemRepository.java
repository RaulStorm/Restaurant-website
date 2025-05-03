package org.example.restaurantwebsite.repository;

import org.example.restaurantwebsite.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    @Query("SELECT mi FROM MenuItem mi")
    List<MenuItem> findAllWithImages();

}
