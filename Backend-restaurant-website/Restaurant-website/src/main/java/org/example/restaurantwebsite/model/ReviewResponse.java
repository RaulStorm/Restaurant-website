package org.example.restaurantwebsite.model;

public class ReviewResponse {
    private String reviewText;
    private int rating;
    private String formattedDate;

    public ReviewResponse() {}

    public ReviewResponse(String reviewText, int rating, String formattedDate) {
        this.reviewText = reviewText;
        this.rating = rating;
        this.formattedDate = formattedDate;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }
}
