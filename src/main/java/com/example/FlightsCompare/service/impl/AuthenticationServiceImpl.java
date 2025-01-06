package com.example.FlightsCompare.service.impl;

import com.example.FlightsCompare.exception.RefreshTokenExpired;
import com.example.FlightsCompare.model.RefreshToken;
import com.example.FlightsCompare.model.User;
import com.example.FlightsCompare.model.dto.OAuth2UserRequestDto;
import com.example.FlightsCompare.model.dto.RefreshAccessTokenPairDto;
import com.example.FlightsCompare.service.AuthenticationService;
import com.example.FlightsCompare.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Override
    public RefreshAccessTokenPairDto authenticate(OAuth2UserRequestDto oAuth2UserRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        oAuth2UserRequestDto.getClientRegistrationId(),
                        oAuth2UserRequestDto.getAccessToken()
                )
        );

        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new RuntimeException("AuthenticationManager returned an unexpected user type");
        }

        RefreshToken refreshTokenDto = tokenService.createRefreshToken(user.getId());
        refreshTokenDto.setUser(user);

        String accessToken = tokenService.generateAccessToken(refreshTokenDto);

        return RefreshAccessTokenPairDto.builder()
                .refreshToken(refreshTokenDto.getRefreshToken())
                .accessToken(accessToken)
                .build();
    }

    @Override
    public String refreshAccessToken(String refreshToken) throws RefreshTokenExpired {
        if (!tokenService.isRefreshTokenValid(refreshToken)) {
            tokenService.deleteRefreshToken(refreshToken);
            throw new RefreshTokenExpired();
        }

        return tokenService.generateAccessToken(tokenService.getRefreshToken(refreshToken, true));
    }

    @Override
    public void logout(UUID userId) {
        tokenService.deleteRefreshToken(userId);
    }
}