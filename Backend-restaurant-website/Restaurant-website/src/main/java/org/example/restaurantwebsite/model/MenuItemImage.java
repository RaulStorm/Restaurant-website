package org.example.restaurantwebsite.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "menu_item_images")
public class MenuItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Если ты НЕ хочешь загружать всю модель MenuItem — просто храни id:
    @Column(name = "menu_item_id")
    private Long menuItemId;

    // Если хочешь получать полную информацию о блюде — подключай объект:
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", insertable = false, updatable = false)
    private MenuItem menuItem;

    @Column(name = "image_url")
    private String imageUrl;
}
