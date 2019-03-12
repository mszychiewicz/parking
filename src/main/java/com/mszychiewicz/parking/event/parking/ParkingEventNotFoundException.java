package com.mszychiewicz.parking.event.parking;

public class ParkingEventNotFoundException extends RuntimeException {

   public ParkingEventNotFoundException(Long id) {
        super("Could not find parking event " + id);
    }
}
