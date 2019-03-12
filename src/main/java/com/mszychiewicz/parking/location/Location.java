package com.mszychiewicz.parking.location;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mszychiewicz.parking.event.parking.ParkingEvent;
import com.mszychiewicz.parking.user.User;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Location {
    private @Id
    @GeneratedValue
    Long id;

    private String address;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="user_id")
    private User owner;

    @JsonBackReference
    @OneToMany(mappedBy = "location")
    private List<ParkingEvent> parkingEvents;

    public Location() {
    }

    public Location(User owner, String address) {
        this.owner = owner;
        this.address = address;
    }
}
