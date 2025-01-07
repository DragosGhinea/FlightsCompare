package com.example.FlightsCompare.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

import java.util.Optional;

/**
 * All the details for the OAuth2 provider are not needed for our use case.
 * We only need the registrationId, the userEndpointURI and the httpMethod.
 * We can use this information to fetch the user details from the OAuth2 provider
 * using the access token
 */
@AllArgsConstructor
@Getter
public enum LiteClientRegistration {

    GITHUB("github", "https://api.github.com/user", HttpMethod.GET),
    DISCORD("discord", "https://discord.com/api/users/@me", HttpMethod.GET)
    ;

    private final String registrationId;
    private final String userEndpointURI;
    private final HttpMethod httpMethod;

    public static Optional<LiteClientRegistration> findByRegistrationId(String registrationId) {
        for (LiteClientRegistration value : values()) {
            if (value.registrationId.equals(registrationId)) {
                return Optional.of(value);
            }
        }

        return Optional.empty();
    }
}