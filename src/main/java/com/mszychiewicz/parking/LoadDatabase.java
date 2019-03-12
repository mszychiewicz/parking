package com.mszychiewicz.parking;

import com.mszychiewicz.parking.location.Location;
import com.mszychiewicz.parking.event.parking.ParkingEvent;
import com.mszychiewicz.parking.location.LocationRepository;
import com.mszychiewicz.parking.user.User;
import com.mszychiewicz.parking.event.parking.ParkingEventRepository;
import com.mszychiewicz.parking.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import static com.mszychiewicz.parking.user.DriverType.*;

@Configuration
@Slf4j
class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(ParkingEventRepository parkingEventRepository, LocationRepository locationRepository, UserRepository userRepository) {
        return args -> {
            User robcio = userRepository.save(new User("Robert", "Kubica", DISABLED));
            log.info("Preloading " + robcio);
            User kimi = userRepository.save(new User("Kimi", "Raikkonen", REGULAR));
            log.info("Preloading " + kimi);
            Location monza = locationRepository.save(new Location(robcio, "Via Vedano, 5, 20900 Monza MB, Italy"));
            log.info("Preloading " + monza);
            Location spa = locationRepository.save(new Location(kimi, "Route du Circuit 55, 4970 Stavelot, Belgium"));
            log.info("Preloading " + spa);
            log.info("Preloading " + parkingEventRepository.save(new ParkingEvent(monza, kimi, "CD1234", Instant.now().minusSeconds(5400), Instant.now())));
            log.info("Preloading " + parkingEventRepository.save(new ParkingEvent(monza, kimi, "CD1234", Instant.now().minusSeconds(54000), Instant.now())));
            log.info("Preloading " + parkingEventRepository.save(new ParkingEvent(spa, robcio, "AB1234", Instant.now().minusSeconds(9000), Instant.now())));
        };
    }
}
