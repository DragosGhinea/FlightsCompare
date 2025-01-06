package com.example.FlightsCompare.service;

import com.example.FlightsCompare.exception.RefreshTokenExpired;
import com.example.FlightsCompare.model.dto.OAuth2UserRequestDto;
import com.example.FlightsCompare.model.dto.RefreshAccessTokenPairDto;

import java.util.UUID;

public interface AuthenticationService {

    RefreshAccessTokenPairDto authenticate(OAuth2UserRequestDto oAuth2UserRequestDTO);

    String refreshAccessToken(String refreshToken) throws RefreshTokenExpired;

    void logout(UUID userId);
}