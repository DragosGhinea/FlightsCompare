package com.example.FlightsCompare.repository;

import com.example.FlightsCompare.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, Long> {


}
