package com.example.FlightsCompare.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UserAccessTokenRequired extends RuntimeException{

    public UserAccessTokenRequired(String message) {
        super(message);
    }

    public UserAccessTokenRequired() {
        super("You need to provide an access token to add credentials to a user with linked providers");
    }
}
