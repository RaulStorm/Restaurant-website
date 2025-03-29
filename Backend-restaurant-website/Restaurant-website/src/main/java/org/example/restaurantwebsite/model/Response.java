package org.example.restaurantwebsite.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Response {
    private boolean success;
    private String message;

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

}
