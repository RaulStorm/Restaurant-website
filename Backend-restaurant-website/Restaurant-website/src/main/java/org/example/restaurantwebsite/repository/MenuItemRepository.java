package org.example.restaurantwebsite.repository;

import org.example.restaurantwebsite.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    @Query("SELECT mi FROM MenuItem mi LEFT JOIN FETCH mi.images WHERE mi.id = :id")
    Optional<MenuItem> findByIdWithImages(@Param("id") Long id);

    @Query("SELECT mi FROM MenuItem mi LEFT JOIN FETCH mi.images")
    List<MenuItem> findAllWithImages();
}



