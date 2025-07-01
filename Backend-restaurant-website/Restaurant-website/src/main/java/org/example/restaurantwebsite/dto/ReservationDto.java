package org.example.restaurantwebsite.dto;

public class ReservationDto {
    private String reservationTime;
    private Integer numberOfPeople;
    private RestaurantTableDto table;
    private String name;

    // геттер и сеттер
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    // Геттеры и сеттеры
    public String getReservationTime() {
        return reservationTime;
    }
    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
    }
    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }
    public void setNumberOfPeople(Integer numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }
    public RestaurantTableDto getTable() {
        return table;
    }
    public void setTable(RestaurantTableDto table) {
        this.table = table;
    }
}