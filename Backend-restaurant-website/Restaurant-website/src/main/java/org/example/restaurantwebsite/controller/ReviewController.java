package org.example.restaurantwebsite.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.restaurantwebsite.model.Review;
import org.example.restaurantwebsite.dto.ReviewDto;
import org.example.restaurantwebsite.model.User;
import org.example.restaurantwebsite.repository.UserRepository;
import org.example.restaurantwebsite.security.JwtTokenUtil;
import org.example.restaurantwebsite.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;

    @Autowired
    public ReviewController(ReviewService reviewService, JwtTokenUtil jwtTokenUtil, UserRepository userRepository) {
        this.reviewService = reviewService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/reviews")
    public ResponseEntity<?> submitReview(@RequestBody ReviewDto reviewDto, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token.");
        }

        String email = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Review review = new Review();
        review.setUser(user);
        review.setReviewText(reviewDto.getReviewText());
        review.setRating(reviewDto.getRating());

        // Данные должны сохраняться
        Review saved = reviewService.saveReview(review);
        return ResponseEntity.ok(saved);
    }


    @GetMapping("/reviews/latest")
    public List<Review> getLatestPositiveReviews() {
        // Получаем топ-5 отзывов с рейтингом больше 4, отсортированных по дате
        return reviewService.findLatestPositiveReviews();
    }
}
