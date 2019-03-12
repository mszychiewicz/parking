package com.mszychiewicz.parking;

import com.mszychiewicz.parking.event.parking.ParkingEvent;
import com.mszychiewicz.parking.event.parking.ParkingEventRepository;
import com.mszychiewicz.parking.location.Location;
import com.mszychiewicz.parking.location.LocationRepository;
import com.mszychiewicz.parking.user.User;
import com.mszychiewicz.parking.user.UserRepository;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.Charset;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.mszychiewicz.parking.user.DriverType.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ParkingApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ParkingEventRepository parkingEventRepository;

    private MediaType contentType = new MediaType("application", "hal+json", Charset.forName("UTF-8"));

    @Test
    public void contextLoads() {
    }

    @Test
    public void givenUser_whenStartParking_thenParkingEventCreated()
            throws Exception {

        User dani = userRepository.save(new User("Daniel", "Ricciardo", REGULAR));
        Location silesia = locationRepository.save(new Location(dani, "Lotnicza, Kamień Śląski, Polska"));
        String registration = "AS2345";

        mvc.perform(post("/users/" + dani.getId() + "/parking")
                .contentType(MediaType.APPLICATION_JSON)
                .param("registration", registration)
                .param("locationId", silesia.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.endTime").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.registration").value(registration));
    }

    @Test
    public void givenStartedParkingEvent_whenLocationCheckActive_thenParkingEventFound()
            throws Exception {

        User dani = userRepository.save(new User("Daniel", "Ricciardo", REGULAR));
        Location silesia = locationRepository.save(new Location(dani, "Lotnicza, Kamień Śląski, Polska"));
        Instant startTime = Instant.now();
        Instant endTime = startTime.plus(2, ChronoUnit.MINUTES);
        String registration = "AS2345";
        ParkingEvent parkingEventActive = parkingEventRepository.save(new ParkingEvent(silesia, dani, registration, startTime, null));
        ParkingEvent parkingEventEnded = parkingEventRepository.save(new ParkingEvent(silesia, dani, registration, startTime, endTime));

        mvc.perform(get("/locations/" + silesia.getId() + "/active")
                .contentType(MediaType.APPLICATION_JSON)
                .param("registration", registration))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.parkingEventList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.parkingEventList[0].id").value(parkingEventActive.getId()))
                .andExpect(jsonPath("$._embedded.parkingEventList[0].endTime").value(parkingEventActive.getEndTime()))
                .andExpect(jsonPath("$._embedded.parkingEventList[0].registration").value(parkingEventActive.getRegistration()))
                .andExpect(jsonPath("$._embedded.parkingEventList[0].location.id").value(silesia.getId()));

    }

    @Test
    public void givenStartedParkingEvent_whenUserStopParking_thenParkingEventHasEndTime()
            throws Exception {

        User dani = userRepository.save(new User("Daniel", "Ricciardo", REGULAR));
        Location silesia = locationRepository.save(new Location(dani, "Lotnicza, Kamień Śląski, Polska"));
        Instant startTime = Instant.now();
        String registration = "AS2345";
        ParkingEvent parkingEvent = parkingEventRepository.save(new ParkingEvent(silesia, dani, registration, startTime, null));

        mvc.perform(patch("/users/" + dani.getId() + "/parking")
                .contentType(MediaType.APPLICATION_JSON)
                .param("eventId", String.valueOf(parkingEvent.getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id").value(parkingEvent.getId()))
                .andExpect(jsonPath("$.endTime").value(Matchers.notNullValue()))
                .andExpect(jsonPath("$.registration").value(registration));

    }

    @Test
    public void whenUserGetBilling_thenRightAmount()
            throws Exception {

        User dani = userRepository.save(new User("Daniel", "Ricciardo", REGULAR));
        Location silesia = locationRepository.save(new Location(dani, "Lotnicza, Kamień Śląski, Polska"));
        Instant startTime = Instant.now();
        String registration = "AS2345";
        ParkingEvent parkingEvent1 = parkingEventRepository.save(new ParkingEvent(silesia, dani, registration, startTime, startTime.plus(4, ChronoUnit.HOURS)));
        ParkingEvent parkingEvent2 = parkingEventRepository.save(new ParkingEvent(silesia, dani, registration, startTime, startTime.plus(3, ChronoUnit.HOURS)));

        mvc.perform(get("/users/" + dani.getId() + "/billing")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.amount").value(parkingEvent1.getCost() + parkingEvent2.getCost()))
                .andExpect(jsonPath("$.currency").value("PLN"));
    }

    @Test
    public void whenUserGetEarnings_thenRightAmount()
            throws Exception {

        User dani = userRepository.save(new User("Daniel", "Ricciardo", REGULAR));
        User lewi = userRepository.save(new User("Lewis", "Hamilton", DISABLED));
        User carl = userRepository.save(new User("Carlos", "Sainz", REGULAR));
        Location silesia = locationRepository.save(new Location(dani, "Lotnicza, Kamień Śląski, Polska"));
        Instant endTimeToday = Instant.now();
        Instant endTimeYesterday = endTimeToday.minus(1, ChronoUnit.DAYS);
        Instant endTimeTheNextDay = endTimeToday.plus(1, ChronoUnit.DAYS);
        String registration = "AS2345";
        ParkingEvent parkingEventToday1 = parkingEventRepository.save(new ParkingEvent(silesia, lewi, registration, endTimeToday.minus(1, ChronoUnit.HOURS), endTimeToday));
        ParkingEvent parkingEventYesterday = parkingEventRepository.save(new ParkingEvent(silesia, lewi, registration, endTimeYesterday.minus(2, ChronoUnit.HOURS), endTimeYesterday));
        ParkingEvent parkingEventToday2 = parkingEventRepository.save(new ParkingEvent(silesia, carl, registration, endTimeToday.minus(3, ChronoUnit.HOURS), endTimeToday));
        ParkingEvent parkingEventTheNextDay = parkingEventRepository.save(new ParkingEvent(silesia, carl, registration, endTimeTheNextDay.minus(4, ChronoUnit.HOURS), endTimeTheNextDay));

        mvc.perform(get("/users/" + dani.getId() + "/earnings")
                .contentType(MediaType.APPLICATION_JSON)
                .param("date", String.valueOf(endTimeToday.truncatedTo(ChronoUnit.DAYS))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.amount").value(parkingEventToday1.getCost() + parkingEventToday2.getCost()))
                .andExpect(jsonPath("$.currency").value("PLN"));
    }
}

