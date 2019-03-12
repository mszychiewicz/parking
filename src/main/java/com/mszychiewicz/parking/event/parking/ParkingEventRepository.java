package com.mszychiewicz.parking.event.parking;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingEventRepository extends JpaRepository<ParkingEvent, Long> {
}
