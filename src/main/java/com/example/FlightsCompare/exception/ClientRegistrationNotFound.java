package com.example.FlightsCompare.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ClientRegistrationNotFound extends RuntimeException{

    public ClientRegistrationNotFound(String message) {
        super(message);
    }

    public ClientRegistrationNotFound() {
        super("Client registration not found!");
    }
}