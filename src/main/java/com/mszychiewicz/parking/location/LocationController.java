package com.mszychiewicz.parking.location;

import com.mszychiewicz.parking.event.parking.ParkingEvent;
import com.mszychiewicz.parking.event.parking.ParkingEventRepository;
import com.mszychiewicz.parking.event.parking.ParkingEventResourceAssembler;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


@RestController
public class LocationController {

    private final LocationRepository locationRepository;
    private final LocationResourceAssembler locationResourceAssembler;
    private final ParkingEventRepository parkingEventRepository;
    private final ParkingEventResourceAssembler parkingEventResourceAssembler;

    LocationController(LocationRepository locationRepository,
                       LocationResourceAssembler locationResourceAssembler,
                       ParkingEventRepository parkingEventRepository,
                       ParkingEventResourceAssembler parkingEventResourceAssembler) {
        this.locationRepository = locationRepository;
        this.locationResourceAssembler = locationResourceAssembler;
        this.parkingEventRepository = parkingEventRepository;
        this.parkingEventResourceAssembler = parkingEventResourceAssembler;
    }

    @GetMapping("/locations")
    public Resources<Resource<Location>> all() {

        List<Resource<Location>> locations = locationRepository.findAll().stream()
                .map(locationResourceAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(locations,
                linkTo(methodOn(LocationController.class).all()).withSelfRel());
    }

    @GetMapping("/locations/{id}")
    public Resource<Location> one(@PathVariable Long id) {

        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new LocationNotFoundException(id));

        return locationResourceAssembler.toResource(location);
    }

    @GetMapping("/locations/{id}/active")
    public Resources<Resource<ParkingEvent>> findParkingEvent(@PathVariable Long id, @RequestParam String registration) {

        List<Resource<ParkingEvent>> parkingEvents = parkingEventRepository.findAll().stream()
                .filter(p -> p.getLocation().getId().equals(id))
                .filter(p -> p.getRegistration().equals(registration))
                .filter(p -> p.getEndTime() == null)
                .map(parkingEventResourceAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(parkingEvents);
    }
}