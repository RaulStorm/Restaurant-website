package org.example.restaurantwebsite.repository;

import org.example.restaurantwebsite.model.Review;
import org.example.restaurantwebsite.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Date;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findTop5ByRatingGreaterThanEqualOrderByCreatedAtDesc(int rating);
    List<Review> findByUserOrderByCreatedAtDesc(User user);
    List<Review> findByCreatedAtBetween(Date startDate, Date endDate);
}
