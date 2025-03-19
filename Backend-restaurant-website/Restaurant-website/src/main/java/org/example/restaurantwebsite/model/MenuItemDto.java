package org.example.restaurantwebsite.model;// MenuItemDto.java

import java.util.List;
import java.util.stream.Collectors;

public class MenuItemDto {

    private Long id;
    private String name;
    private String description;
    private String categoryName;
    private Double price;
    private List<String> images;

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
    private MenuItemDto convertToDto(MenuItem menuItem) {
        MenuItemDto dto = new MenuItemDto();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setCategoryName(menuItem.getCategory().getName());
        dto.setPrice(menuItem.getPrice());
        dto.setImages(menuItem.getImages().stream()
                .map(MenuItemImage::getImageUrl)
                .collect(Collectors.toList()));
        return dto;
    }

}
