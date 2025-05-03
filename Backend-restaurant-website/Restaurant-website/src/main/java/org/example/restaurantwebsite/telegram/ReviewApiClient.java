package org.example.restaurantwebsite.telegram;

import org.example.restaurantwebsite.dto.ReviewDto;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReviewApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public List<ReviewBot> fetchLatestPositiveReviews() {
        String url = "http://localhost:8080/api/reviews/latest";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String json = response.getBody();

        JSONArray array = new JSONArray(json);
        List<ReviewBot> reviews = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            ReviewBot r = new ReviewBot();

            if (obj.has("reviewText")) r.setReviewText(obj.getString("reviewText"));
            if (obj.has("rating")) r.setRating(obj.getInt("rating"));
            if (obj.has("formattedDate")) r.setFormattedDate(obj.getString("formattedDate"));

            if (obj.has("user")) {
                JSONObject user = obj.getJSONObject("user");
                if (user.has("name")) {
                    r.setUserName(user.getString("name"));
                }
            }

            reviews.add(r);
        }

        return reviews;
    }

    public ResponseEntity<?> submitReview(ReviewDto reviewDto, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<ReviewDto> request = new HttpEntity<>(reviewDto, headers);
            return restTemplate.postForEntity("http://localhost:8080/api/reviews", request, Object.class);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
