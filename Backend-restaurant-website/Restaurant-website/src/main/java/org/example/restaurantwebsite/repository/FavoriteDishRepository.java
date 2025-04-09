package org.example.restaurantwebsite.repository;

import org.example.restaurantwebsite.model.MenuItem;
import org.example.restaurantwebsite.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavoriteDishRepository extends JpaRepository<MenuItem, Long> {
    // Предполагается, что у вас есть связь между пользователем и любимыми блюдами
//    List<MenuItem> findByUser(User user);
}
