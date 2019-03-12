package com.mszychiewicz.parking.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mszychiewicz.parking.event.parking.ParkingEvent;
import com.mszychiewicz.parking.location.Location;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class User {
    private @Id
    @GeneratedValue
    Long id;

    private String firstName;

    private String lastName;

    private DriverType driverType;

    @JsonBackReference()
    @OneToMany(mappedBy = "user")
    private List<ParkingEvent> parkingEvents;

    @JsonBackReference()
    @OneToMany(mappedBy = "owner")
    private List<Location> locations;

    public User() {
    }

    public User(String firstName, String lastName, DriverType driverType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.driverType = driverType;
    }
}