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


//Generated with GPT
@SpringBootTest
@AutoConfigureMockMvc
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightRepository flightRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

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
        mockMvc.perform(get("/allFlightsLowerThan/{price}", 150.0))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json")) // Ensure the response type is JSON
                .andExpect(jsonPath("$").isArray()) // Ensure the response is an array
                .andExpect(jsonPath("$.length()").value(2)) // Two flights match the criteria
                .andExpect(jsonPath("$[0].id").value(100)) // First flight
                .andExpect(jsonPath("$[1].id").value(200)); // Second flight
    }
}