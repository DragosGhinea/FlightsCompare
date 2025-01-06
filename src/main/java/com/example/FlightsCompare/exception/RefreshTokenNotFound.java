package com.example.FlightsCompare.exception;

public class RefreshTokenNotFound extends RuntimeException{

    public RefreshTokenNotFound(String message) {
        super(message);
    }

    public RefreshTokenNotFound() {
        super("Refresh token not found!");
    }
}