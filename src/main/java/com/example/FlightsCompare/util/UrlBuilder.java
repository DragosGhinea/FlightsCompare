package com.example.FlightsCompare.util;

import com.example.FlightsCompare.model.Flight;
import org.springframework.stereotype.Component;

@Component
public class UrlBuilder {

    public String buildUrlBasedOnArguments(Flight flight) {

        String from = flight.getDepartureAbbreviation();
        String to = flight.getArrivalAbbreviation();
        String year = flight.getYearValue();
        String month = flight.getMonthValue();
        String day = flight.getDayValue();

        String url = String.format("https://www.ryanair.com/ro/ro/trip/flights/select?adults=1&teens=0&children=0&infants=0" +
                        "&dateOut=%s-%s-%s" +
                        "&dateIn=%s-%s-%s&isConnectedFlight=false&discount=0&promoCode=&isReturn=true" +
                        "&originIata=%s&destinationIata=%s&tpAdults=1&tpTeens=0&tpChildren=0&tpInfants=0" +
                        "&tpStartDate=%s-%s-%s" +
                        "&tpEndDate=%s-%s-%s&tpDiscount=0&tpPromoCode=" +
                        "&tpOriginIata=%s&tpDestinationIata=%s",
                year, month, day,
                year, month, day,
                from, to,
                year, month, day,
                year, month, day,
                from, to);

        return url;
    }
}
