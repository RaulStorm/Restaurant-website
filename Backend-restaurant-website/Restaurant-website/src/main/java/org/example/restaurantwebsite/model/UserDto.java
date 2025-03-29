package org.example.restaurantwebsite.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDto {
    private String name;
    private String email;
    private String password;

    @Override
    public String toString() {
        return "UserDto{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
