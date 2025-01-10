package com.example.FlightsCompare.service;

import com.example.FlightsCompare.model.User;

public interface CredentialsToUserService {

    User login(String emailOrUsername, String password);

    User register(String username, String email, String password);

    User register(String username, String email, String password, String accessToken);
}
