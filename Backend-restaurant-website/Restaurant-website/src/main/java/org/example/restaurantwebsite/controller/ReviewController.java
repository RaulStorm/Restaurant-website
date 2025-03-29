package org.example.restaurantwebsite.controller;

import org.example.restaurantwebsite.model.Review;
import org.example.restaurantwebsite.service.ReviewService;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/api/reviews")
    public Review submitReview(@RequestBody Review review) {
        return reviewService.saveReview(review);
    }
}
