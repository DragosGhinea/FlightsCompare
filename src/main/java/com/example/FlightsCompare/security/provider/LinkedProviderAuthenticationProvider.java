package com.example.FlightsCompare.security.provider;

import com.example.FlightsCompare.model.User;
import com.example.FlightsCompare.model.dto.OAuth2UserRequestDto;
import com.example.FlightsCompare.security.tokens.LinkedProviderToken;
import com.example.FlightsCompare.service.impl.OAuth2ToUserServiceCentralized;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@RequiredArgsConstructor
public class LinkedProviderAuthenticationProvider implements AuthenticationProvider {

    private final OAuth2ToUserServiceCentralized authenticationService;

    /**
     * Receive UsernamePasswordAuthenticationToken as LinkedProviderToken
     * where username is the provider
     * where password is the access_token
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String provider = authentication.getName();
        final String token = authentication.getCredentials().toString();

        User user = authenticationService.getUserFromOAuth2(OAuth2UserRequestDto.builder()
                .clientRegistrationId(provider)
                .accessToken(token)
                .build()
        );

        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(LinkedProviderToken.class);
    }
}