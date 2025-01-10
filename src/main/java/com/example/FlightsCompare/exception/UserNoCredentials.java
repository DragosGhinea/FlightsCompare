package com.example.FlightsCompare.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserNoCredentials extends RuntimeException{

    public UserNoCredentials(String message) {
        super(message);
    }

    public UserNoCredentials() {
        super("User exists but does not have credentials!");
    }
}