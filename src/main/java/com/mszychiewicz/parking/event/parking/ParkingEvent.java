package com.mszychiewicz.parking.event.parking;

import com.fasterxml.jackson.annotation.*;
import com.mszychiewicz.parking.location.Location;
import com.mszychiewicz.parking.payments.CostCalculator;
import com.mszychiewicz.parking.user.User;
import lombok.Data;

import javax.persistence.*;
import java.time.Instant;


@Data
@Entity
public class ParkingEvent {
    private @Id
    @GeneratedValue
    Long id;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne
    private Location location;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    private String registration;

    private Instant startTime;

    private Instant endTime;

    private double cost;

    public ParkingEvent() {
    }

    public ParkingEvent(Location location, User user, String registration, Instant startTime, Instant endTime) {
        this.location = location;
        this.user = user;
        this.registration = registration;
        this.startTime = startTime;
        this.endTime = endTime;
        if (endTime != null) {
            cost = CostCalculator.calculateCost(startTime, endTime, user.getDriverType());
        } else {
            cost = 0.0;
        }
    }


    public ParkingEvent(Location location, User user, String registration) {
        this.location = location;
        this.user = user;
        this.registration = registration;
        this.startTime = Instant.now();
        this.endTime = null;
        this.cost = 0.0;
    }
}
