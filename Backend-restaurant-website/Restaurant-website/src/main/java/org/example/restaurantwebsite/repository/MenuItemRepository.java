package org.example.restaurantwebsite.repository;

import org.example.restaurantwebsite.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    // Убираем LEFT JOIN FETCH, так как атрибут images больше не существует
    @Query("SELECT mi FROM MenuItem mi WHERE mi.id = :id")
    Optional<MenuItem> findByIdWithImages(@Param("id") Long id);

    // Убираем LEFT JOIN FETCH, так как атрибут images больше не существует
    @Query("SELECT mi FROM MenuItem mi")
    List<MenuItem> findAllWithImages();

    Optional<MenuItem> findByName(String name);

}
