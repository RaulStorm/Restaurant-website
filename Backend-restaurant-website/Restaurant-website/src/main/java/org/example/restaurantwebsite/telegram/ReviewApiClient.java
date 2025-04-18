package org.example.restaurantwebsite.telegram;

import org.example.restaurantwebsite.model.Review;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
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
}
