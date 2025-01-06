package com.example.FlightsCompare.service.impl;

import com.example.FlightsCompare.exception.RefreshTokenNotFound;
import com.example.FlightsCompare.exception.UserNotFound;
import com.example.FlightsCompare.model.RefreshToken;
import com.example.FlightsCompare.model.User;
import com.example.FlightsCompare.repository.RefreshTokenRepository;
import com.example.FlightsCompare.repository.UserRepository;
import com.example.FlightsCompare.security.JwtService;
import com.example.FlightsCompare.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * In this implementation, only a refresh token per user is allowed.
 * A single access token is allowed as well. When generating a new access token, the old one is invalidated.
 * The invalidation is done via the lastRefresh field of the refresh token.
 */
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Override
    public boolean isAccessTokenValid(RefreshToken refreshToken, String accessToken) {
        Long issuedAt = jwtService.extractIssuedAt(accessToken).toInstant().getEpochSecond();

        return refreshToken.getLastRefresh() <= issuedAt && !jwtService.isTokenExpired(accessToken);
    }

    @Override
    public boolean isRefreshTokenValid(String refreshToken) {
        return !jwtService.isTokenExpired(refreshToken);
    }

    @Override
    public RefreshToken getRefreshToken(UUID userId, boolean includeUser) throws RefreshTokenNotFound {
        return (includeUser?
                refreshTokenRepository.findByUserIdWithUser(userId) : refreshTokenRepository.findByUserId(userId))
                .orElseThrow(() -> new RefreshTokenNotFound("Refresh token not found for user with id " + userId));
    }

    @Override
    public RefreshToken getRefreshToken(String refreshToken, boolean includeUser) throws RefreshTokenNotFound {
        return (includeUser?
                refreshTokenRepository.findByRefreshTokenWithUser(refreshToken) : refreshTokenRepository.findByRefreshToken(refreshToken))
                .orElseThrow(() -> new RefreshTokenNotFound("Refresh token not found"));
    }

    @Transactional
    @Override
    public RefreshToken createRefreshToken(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound("User with id " + userId + " not found"));
        String refreshToken = jwtService.generateRefreshToken(user);
        RefreshToken newRefreshToken = RefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .lastRefresh(Instant.now().getEpochSecond())
                .build();

        newRefreshToken = refreshTokenRepository.save(newRefreshToken);
        return newRefreshToken;
    }

    @Transactional
    @Override
    public boolean deleteRefreshToken(UUID userId) {
        return refreshTokenRepository.deleteByUserId(userId) != 0;
    }

    @Transactional
    @Override
    public boolean deleteRefreshToken(String refreshToken) {
        return refreshTokenRepository.deleteByRefreshToken(refreshToken) != 0;
    }

    @Transactional
    @Override
    public String generateAccessToken(RefreshToken refreshToken) {
        if (refreshToken.getUser() == null) {
            throw new IllegalArgumentException("User must be set in the refresh token");
        }

        refreshToken.setLastRefresh(Instant.now().getEpochSecond());
        refreshTokenRepository.save(refreshToken);

        return jwtService.generateAccessToken(refreshToken.getUser());
    }
}