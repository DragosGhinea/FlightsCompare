package com.example.FlightsCompare.controller;

import com.example.FlightsCompare.model.Flight;
import com.example.FlightsCompare.repository.FlightRepository;
import com.example.FlightsCompare.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/flight")
@CrossOrigin(origins = "*")
public class FlightController {


    @Autowired
    FlightRepository flightRepository;
    @Autowired
    FlightService flightService;

    @GetMapping("/allFlights")
    public List<Flight> getAllFlights() {

        return flightRepository.findAll();
    }

    @GetMapping("/allFlightsTo/{city}")
    public List<Flight> getAllFlightsToCity(@PathVariable(name = "city") String city) {

        List<Flight> allFlightsToCity = flightRepository
                .findAll()
                .stream()
                .filter(flight -> flight.getArrivalCity().equals(city))
                .collect(Collectors.toList());

        return allFlightsToCity;
    }

    @PostMapping("/addFlight")
    public ResponseEntity<Flight> addFlight(@RequestBody Flight flight) {

        flightService.setFlightDuration(flight);
        Flight savedFlight = flightRepository.save(flight);

        return ResponseEntity.ok(savedFlight);
    }


}
