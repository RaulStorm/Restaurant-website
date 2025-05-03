package org.example.restaurantwebsite.dto;

public class ReservationWithIdDto extends ReservationDto {
    private Long id;
    private Integer durationHours;


    public ReservationWithIdDto() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Integer getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(Integer durationHours) {
        this.durationHours = durationHours;
    }

}

