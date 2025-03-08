package org.example.restaurantwebsite.controller;

import org.example.restaurantwebsite.model.Review;
import org.example.restaurantwebsite.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping("/api/reviews")
    public Review submitReview(@RequestBody Review review) {
        return reviewService.saveReview(review);
    }
}
