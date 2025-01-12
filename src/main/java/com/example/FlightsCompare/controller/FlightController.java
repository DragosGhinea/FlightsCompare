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

    @GetMapping("/allFlightsLowerThan/{price}")
    public List<Flight> getAllFlightsLowerThan(@PathVariable(name = "price") Double price) {

        List<Flight> allFlightsCheaperThanPrice = flightRepository
                .findAll()
                .stream()
                .filter(flight -> flight.getPrice() <= price)
                .toList();

        return allFlightsCheaperThanPrice;
    }

    @GetMapping("/allFlightsGreaterThan/{price}")
    public List<Flight> getAllFlightsGreaterThan(@PathVariable(name = "price") Double price) {

        List<Flight> allFlightsGreaterThanPrice = flightRepository
                .findAll()
                .stream()
                .filter(flight -> flight.getPrice() >= price)
                .toList();

        return allFlightsGreaterThanPrice;
    }

    @GetMapping("/allFlightsOnDate")
    public List<Flight> getAllFlightsOnDate(@RequestParam("year") String year,
                                            @RequestParam("month") String month,
                                            @RequestParam("day") String day) {

        List<Flight> allFlightsOnDate = flightRepository
                .findAll()
                .stream()
                .filter(flight -> flight.getYearValue().equals(year) &&
                                  flight.getMonthValue().equals(month) &&
                                  flight.getDayValue().equals(day)
                       )
                .toList();

        return allFlightsOnDate;
    }

    @GetMapping("/allFlightsWithDetails")
    public List<Flight> getAllFlightsWithDetails(@RequestParam("departureCity") String departureCity,
                                                 @RequestParam("arrivalCity") String arrivalCity,
                                                 @RequestParam("year") String year,
                                                 @RequestParam("month") String month,
                                                 @RequestParam("day") String day){
        List<Flight> allFlightsWithDetails = flightRepository
                .findAll()
                .stream()
                .filter(flight -> flight.getDepartureCity().equals(departureCity) &&
                                  flight.getArrivalCity().equals(arrivalCity) &&
                                  flight.getYearValue().equals(year) &&
                                  flight.getMonthValue().equals(month) &&
                                  flight.getDayValue().equals(day)
                       )
                .toList();

        return allFlightsWithDetails;
    }

    //Getting all the flights with a similar flightDuration
    // +- 60 minutes in this case
    @GetMapping("/allFlightsWithFlightDuration/{flightDuration}")
    public List<Flight> getAllFlightsWithSimilarDuration(@PathVariable(name = "flightDuration") Integer flightDuration) {

        final Integer approximationInMinutes = 60;

        List<Flight> allFlightsWithSimilarDuration = flightRepository
                .findAll()
                .stream()
                .filter(flight -> flight.getFlightDuration() <= flightDuration + approximationInMinutes &&
                                  flight.getFlightDuration() >= flightDuration - approximationInMinutes
                       )
                .toList();

        return allFlightsWithSimilarDuration;
    }

    @PostMapping("/addFlight")
    public ResponseEntity<Flight> addFlight(@RequestBody Flight flight) {

        flightService.setFlightDuration(flight);
        Flight savedFlight = flightRepository.save(flight);

        return ResponseEntity.ok(savedFlight);
    }


}
