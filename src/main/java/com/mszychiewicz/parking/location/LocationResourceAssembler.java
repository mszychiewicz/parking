package com.mszychiewicz.parking.location;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.Resource;

import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

@Component
public class LocationResourceAssembler implements ResourceAssembler<Location, Resource<Location>> {

    @Override
    public Resource<Location> toResource(Location location) {
        return new Resource<>(location,
                linkTo(methodOn(LocationController.class).one(location.getId())).withSelfRel(),
                linkTo(methodOn(LocationController.class).all()).withRel("location"));
    }
}
