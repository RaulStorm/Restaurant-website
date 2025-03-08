package org.example.restaurantwebsite.repository;

import org.example.restaurantwebsite.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
