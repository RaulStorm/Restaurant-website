package org.example.restaurantwebsite.repository;

import org.example.restaurantwebsite.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findTop5ByRatingGreaterThanEqualOrderByCreatedAtDesc(int rating);

}
