package org.example.restaurantwebsite.service;

import org.example.restaurantwebsite.model.Review;
import org.example.restaurantwebsite.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }
}
