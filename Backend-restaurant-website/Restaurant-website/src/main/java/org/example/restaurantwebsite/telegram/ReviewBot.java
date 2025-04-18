package org.example.restaurantwebsite.telegram;

public class ReviewBot {
    private String reviewText;
    private int rating;
    private String userName;
    private String formattedDate;

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

    public String getUserName() {
        return userName != null ? userName : "Гость";
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFormattedDate() {
        return formattedDate != null ? formattedDate : "дата не указана";
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }
}
