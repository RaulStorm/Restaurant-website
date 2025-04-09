package org.example.restaurantwebsite.model;

public class ReservationWithIdDto extends ReservationDto {
    private Long id;

    public ReservationWithIdDto() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
