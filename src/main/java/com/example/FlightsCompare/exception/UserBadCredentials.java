package com.example.FlightsCompare.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UserBadCredentials extends RuntimeException{

    public UserBadCredentials(String message) {
        super(message);
    }

    public UserBadCredentials() {
        super("Incorrect credentials!");
    }
}