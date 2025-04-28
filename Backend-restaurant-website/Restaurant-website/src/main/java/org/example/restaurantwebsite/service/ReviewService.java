package org.example.restaurantwebsite.service;

import jakarta.transaction.Transactional;
import org.example.restaurantwebsite.model.Review;
import org.example.restaurantwebsite.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    //==================================
    // Метод для получения отзывов за выбранный период
    public List<Review> getReviewsForPeriod(Date startDate, Date endDate) {
        return reviewRepository.findByCreatedAtBetween(startDate, endDate);
    }

    // Метод для вычисления среднего рейтинга
    public double getAverageRating(List<Review> reviews) {
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0);
    }

    // Метод для вычисления процентного соотношения отзывов по каждому рейтингу (1-5)
    public double[] getRatingPercentages(List<Review> reviews) {
        double[] percentages = new double[5];  // для оценок от 1 до 5
        double totalReviews = reviews.size();

        for (Review review : reviews) {
            if (review.getRating() >= 1 && review.getRating() <= 5) {
                percentages[review.getRating() - 1]++;
            }
        }

        // Преобразуем в проценты
        for (int i = 0; i < 5; i++) {
            percentages[i] = (percentages[i] / totalReviews) * 100;
        }

        return percentages;
    }
}
