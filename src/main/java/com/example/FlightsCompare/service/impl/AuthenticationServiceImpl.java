package com.example.FlightsCompare.service.impl;

import com.example.FlightsCompare.exception.RefreshTokenExpired;
import com.example.FlightsCompare.model.RefreshToken;
import com.example.FlightsCompare.model.User;
import com.example.FlightsCompare.model.dto.LoginRequestDto;
import com.example.FlightsCompare.model.dto.OAuth2UserRequestDto;
import com.example.FlightsCompare.model.dto.RefreshAccessTokenPairDto;
import com.example.FlightsCompare.model.dto.RegisterRequestDto;
import com.example.FlightsCompare.security.tokens.LinkedProviderToken;
import com.example.FlightsCompare.security.tokens.LoginToken;
import com.example.FlightsCompare.security.tokens.RegisterToken;
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

    private RefreshAccessTokenPairDto fromAuthentication(Authentication authentication) {
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
    public RefreshAccessTokenPairDto authenticate(OAuth2UserRequestDto oAuth2UserRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new LinkedProviderToken(
                        oAuth2UserRequestDto.getClientRegistrationId(),
                        oAuth2UserRequestDto.getAccessToken()
                )
        );

        return fromAuthentication(authentication);
    }

    @Override
    public RefreshAccessTokenPairDto authenticate(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new LoginToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                )
        );

        return fromAuthentication(authentication);
    }

    @Override
    public RefreshAccessTokenPairDto authenticate(RegisterRequestDto registerRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new RegisterToken(
                        registerRequestDto.getUsername(),
                        registerRequestDto.getEmail(),
                        registerRequestDto.getPassword(),
                        registerRequestDto.getAccessToken()
                )
        );

        return fromAuthentication(authentication);
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