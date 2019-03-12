package com.mszychiewicz.parking.user;

import com.mszychiewicz.parking.event.parking.ParkingEvent;
import com.mszychiewicz.parking.event.parking.ParkingEventNotFoundException;
import com.mszychiewicz.parking.event.parking.ParkingEventRepository;
import com.mszychiewicz.parking.event.parking.ParkingEventResourceAssembler;
import com.mszychiewicz.parking.location.Location;
import com.mszychiewicz.parking.location.LocationRepository;
import com.mszychiewicz.parking.location.LocationResourceAssembler;
import com.mszychiewicz.parking.payments.CostCalculator;
import com.mszychiewicz.parking.payments.Money;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {
    private final UserRepository userRepository;
    private final UserResourceAssembler userResourceAssembler;
    private final ParkingEventRepository parkingEventRepository;
    private final ParkingEventResourceAssembler parkingEventResourceAssembler;
    private final LocationRepository locationRepository;
    private final LocationResourceAssembler locationResourceAssembler;

    public UserController(UserRepository userRepository,
                          UserResourceAssembler userEventResourceAssembler,
                          ParkingEventRepository parkingEventRepository,
                          ParkingEventResourceAssembler parkingEventResourceAssembler,
                          LocationRepository locationRepository,
                          LocationResourceAssembler locationResourceAssembler) {
        this.userRepository = userRepository;
        this.userResourceAssembler = userEventResourceAssembler;
        this.parkingEventRepository = parkingEventRepository;
        this.parkingEventResourceAssembler = parkingEventResourceAssembler;
        this.locationRepository = locationRepository;
        this.locationResourceAssembler = locationResourceAssembler;
    }

    @GetMapping("/users")
    public Resources<Resource<User>> all() {
        List<Resource<User>> users = userRepository.findAll().stream()
                .map(userResourceAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(users);
    }

    @PostMapping("/users")
    ResponseEntity<?> newUser(@RequestBody User newUser) throws URISyntaxException {

        Resource<User> resource = userResourceAssembler.toResource(userRepository.save(newUser));

        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    @GetMapping("/users/{id}")
    public Resource<User> one(@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return userResourceAssembler.toResource(user);
    }

    @GetMapping("/users/{Id}/locations")
    public Resources<Resource<Location>> getLocations(@PathVariable Long Id) {

        List<Resource<Location>> locations = userRepository.getOne(Id).getLocations().stream()
                .map(locationResourceAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(locations);
    }

    @GetMapping("/users/{Id}/parking")
    Resources<Resource<ParkingEvent>> getParkingEvents(@PathVariable Long Id) {

        List<Resource<ParkingEvent>> parkingEvents = userRepository.getOne(Id).getParkingEvents().stream()
                .map(parkingEventResourceAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(parkingEvents);
    }

    @PostMapping("/users/{Id}/parking")
    Resource<ParkingEvent> startParkingEvent(@PathVariable Long Id, @RequestParam String registration, @RequestParam Long locationId) throws URISyntaxException {

        return parkingEventResourceAssembler.toResource(parkingEventRepository.save(new ParkingEvent(locationRepository.getOne(locationId), userRepository.getOne(Id), registration)));
    }

    @PatchMapping("/users/{Id}/parking")
    Resource<ParkingEvent> endParkingEvent(@RequestParam Long eventId) throws URISyntaxException {

        return parkingEventRepository.findById(eventId)
                .map(parkingEvent -> {
                    parkingEvent.setEndTime(Instant.now());
                    parkingEvent.setCost(CostCalculator.calculateCost(parkingEvent));
                    return parkingEventResourceAssembler.toResource(parkingEventRepository.save(parkingEvent));
                })
                .orElseThrow(() -> new ParkingEventNotFoundException(eventId));
    }

    @GetMapping("/users/{id}/earnings")
    public Resource<Money> oneDayEarnings(@PathVariable Long id, @RequestParam Instant date) {

        Double earnings = userRepository.getOne(id).getLocations().stream()
                .flatMap(l -> l.getParkingEvents().stream())
                .filter(p -> p.getEndTime().isAfter(date) && p.getEndTime().isBefore(date.plus(24, ChronoUnit.HOURS)))
                .mapToDouble(ParkingEvent::getCost)
                .sum();
        return new Resource<>(new Money(earnings, "PLN"));
    }

    @GetMapping("/users/{id}/billing")
    public Resource<Money> getBalance(@PathVariable Long id) {

        Double billing = parkingEventRepository.findAll().stream()
                .filter(p -> p.getUser().getId().equals(id))
                .mapToDouble(ParkingEvent::getCost)
                .sum();
        return new Resource<>(new Money(billing, "PLN"));
    }
}


