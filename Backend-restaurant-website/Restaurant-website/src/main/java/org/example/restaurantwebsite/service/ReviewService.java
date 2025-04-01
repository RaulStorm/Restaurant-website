package org.example.restaurantwebsite.service;

import jakarta.transaction.Transactional;
import org.example.restaurantwebsite.model.Review;
import org.example.restaurantwebsite.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }


    @Transactional
    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }







    // Возвращаем отзывы с рейтингом больше 4, отсортированные по дате
    public List<Review> findLatestPositiveReviews() {
        return reviewRepository.findTop5ByRatingGreaterThanEqualOrderByCreatedAtDesc(4);
    }
}
