package org.example.restaurantwebsite.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// MenuItemImage.java
@Setter
@Getter
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

}
