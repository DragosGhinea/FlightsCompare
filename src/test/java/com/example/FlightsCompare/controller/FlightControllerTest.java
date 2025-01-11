package com.example.FlightsCompare.controller;

import com.example.FlightsCompare.model.Flight;
import com.example.FlightsCompare.repository.FlightRepository;
import com.example.FlightsCompare.security.JwtAuthenticationFilter;
import com.example.FlightsCompare.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FlightRepository flightRepository;

    //Generated with GPT, to see an example

    @Test
    void testGetAllFlightsLowerThan() throws Exception {
        // Create mock Flight objects
        Flight flight1 = new Flight();
        flight1.setId(100);
        flight1.setPrice(100.00);

        Flight flight2 = new Flight();
        flight2.setId(200);
        flight2.setPrice(150.00);

        Flight flight3 = new Flight();
        flight3.setId(300);
        flight3.setPrice(200.00);

        List<Flight> allFlights = Arrays.asList(flight1, flight2, flight3);

        // Mock repository behavior
        when(flightRepository.findAll()).thenReturn(allFlights);

        // Perform the GET request for flights with price <= 150
        mockMvc.perform(get("/flight/allFlightsLowerThan/{price}", 150.0))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json")) // Ensure the response type is JSON
                .andExpect(jsonPath("$").isArray()) // Ensure the response is an array
                .andExpect(jsonPath("$.length()").value(2)) // Two flights match the criteria
                .andExpect(jsonPath("$[0].id").value(100)) // First flight
                .andExpect(jsonPath("$[1].id").value(200)); // Second flight
    }

    //facut dupa exemplu, dar adaptat la rutele respective
    @Test
    void testGetAllFlightsToCity() throws Exception {

        //Create mock Flight objects
        Flight flight1 = new Flight();
        flight1.setArrivalCity("Bucharest");

        Flight flight2 = new Flight();
        flight2.setArrivalCity("London");

        Flight flight3 = new Flight();
        flight3.setArrivalCity("Bucharest");

        List<Flight> allFlights = Arrays.asList(flight1, flight2, flight3);

        //Mock repo behavior
        when(flightRepository.findAll()).thenReturn(allFlights);

        //Perform GET request with "Bucharest" as the target city.
        mockMvc.perform(get("/flight/allFlightsTo/{city}", "Bucharest"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].arrivalCity").value("Bucharest"))
                .andExpect(jsonPath("$[1].arrivalCity").value("Bucharest"));
    }

    @Test
    void testGetAllFlightsOnDate() throws Exception {

        //create mock Flight objects
        Flight flight1 = new Flight();
        flight1.setYearValue("2025");
        flight1.setMonthValue("01");
        flight1.setDayValue("12");

        Flight flight2 = new Flight();
        flight2.setYearValue("2024");
        flight2.setMonthValue("01");
        flight2.setDayValue("12");

        Flight flight3 = new Flight();
        flight3.setYearValue("2025");
        flight3.setMonthValue("02");
        flight3.setDayValue("12");

        List<Flight> allFlights = Arrays.asList(flight1, flight2, flight3);

        //Mock repo
        when(flightRepository.findAll()).thenReturn(allFlights);

        //Perform GET request with "2025" as the target year and month "01" as target.
        mockMvc.perform(get("/flight/allFlightsOnDate")
                .param("year","2025")
                .param("month","01")
                .param("day","12"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))//we are only looking for 1 flight in this case
                .andExpect(jsonPath("$[0].yearValue").value("2025"))
                .andExpect(jsonPath("$[0].monthValue").value("01"))
                .andExpect(jsonPath("$[0].dayValue").value("12"));

    }
}