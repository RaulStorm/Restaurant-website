package org.example.restaurantwebsite.model;

import jakarta.persistence.*;
// MenuItemImage.java
@Entity
@Table(name = "menu_item_images")
public class MenuItemImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "menu_item_id")
    private Long menuItemId;

    @ManyToOne
    @JoinColumn(name = "menu_item_id", insertable = false, updatable = false)
    private MenuItem menuItem;

    @Column(name = "image_url")
    private String imageUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
// Getters and Setters
}
