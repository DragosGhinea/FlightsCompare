package com.example.FlightsCompare.service;

import com.example.FlightsCompare.model.User;

public interface OAuth2ToUserService {

    User getUserFromOAuth2(String accessToken);

}