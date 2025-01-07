package com.example.FlightsCompare.service;

import com.example.FlightsCompare.exception.RefreshTokenNotFound;
import com.example.FlightsCompare.model.RefreshToken;

import java.util.UUID;

public interface TokenService {

    boolean isAccessTokenValid(RefreshToken refreshToken, String accessToken);

    boolean isRefreshTokenValid(String refreshToken);

    RefreshToken getRefreshToken(UUID userId, boolean includeUser) throws RefreshTokenNotFound;

    RefreshToken getRefreshToken(String refreshToken, boolean includeUser) throws RefreshTokenNotFound;

    RefreshToken createRefreshToken(UUID userId);

    boolean deleteRefreshToken(UUID userId);

    boolean deleteRefreshToken(String refreshToken);

    String generateAccessToken(RefreshToken refreshToken);
}