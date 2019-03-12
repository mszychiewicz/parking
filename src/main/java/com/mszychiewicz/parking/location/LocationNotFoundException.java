package com.mszychiewicz.parking.location;

class LocationNotFoundException extends RuntimeException {
    LocationNotFoundException(Long id) {
        super("Could not find location " + id);
    }
}
