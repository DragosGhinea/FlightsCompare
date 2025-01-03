package com.example.FlightsCompare.util;

import com.example.FlightsCompare.model.Flight;
import org.junit.jupiter.api.Test;

class UrlBuilderTest {

    @Test
    void buildUrlBasedOnArguments() {

        UrlBuilder urlBuilder = new UrlBuilder();

        Flight flight = new Flight();
        flight.setDepartureAbbreviation("OTP");
        flight.setArrivalAbbreviation("BHX");
        flight.setYearValue("2024");
        flight.setMonthValue("12");
        flight.setDayValue("23");
        String url = urlBuilder.buildUrlBasedOnArguments(flight);

        System.out.println(url);
    }
}