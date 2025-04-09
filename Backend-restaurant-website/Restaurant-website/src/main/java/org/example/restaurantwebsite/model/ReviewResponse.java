package org.example.restaurantwebsite.model;

import lombok.Data;

@Data
public class ReviewResponse {
    private String reviewText;

    public ReviewResponse(Object review) {
        this.reviewText = (review != null) ? review.toString() : "";
    }
}
