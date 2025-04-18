package org.example.restaurantwebsite.telegram;

import org.example.restaurantwebsite.model.MenuItemDto;
import java.util.List;

public class UserMenuSession {
    private String category;
    private int currentIndex;
    private List<MenuItemDto> items;

    public UserMenuSession(String category, int currentIndex, List<MenuItemDto> items) {
        this.category = category;
        this.currentIndex = currentIndex;
        this.items = items;
    }

    public String getCategory() {
        return category;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public List<MenuItemDto> getItems() {
        return items;
    }
}
