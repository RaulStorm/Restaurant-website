package org.example.restaurantwebsite.dto;

public class ReservationWithIdDto extends ReservationDto {
    private Long id;
    private Integer durationHours;
    private String reservationEndTime;  // Новое поле


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

    public String getReservationEndTime() {
        return reservationEndTime;
    }

    public void setReservationEndTime(String reservationEndTime) {
        this.reservationEndTime = reservationEndTime;
    }
}

