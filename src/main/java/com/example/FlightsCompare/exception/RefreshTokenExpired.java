package com.example.FlightsCompare.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class RefreshTokenExpired extends RuntimeException{

    public RefreshTokenExpired() {
        super("Refresh token has expired!");
    }
}