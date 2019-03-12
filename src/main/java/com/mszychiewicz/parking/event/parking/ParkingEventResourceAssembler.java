package com.mszychiewicz.parking.event.parking;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

@Component
public class ParkingEventResourceAssembler implements ResourceAssembler<ParkingEvent, Resource<ParkingEvent>> {

    @Override
    public Resource<ParkingEvent> toResource(ParkingEvent parkingEvent) {
        return new Resource<>(parkingEvent);
    }

}
