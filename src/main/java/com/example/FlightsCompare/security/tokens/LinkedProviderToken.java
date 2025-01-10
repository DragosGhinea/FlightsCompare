package com.example.FlightsCompare.security.tokens;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
@Setter
public class LinkedProviderToken extends UsernamePasswordAuthenticationToken {

    public LinkedProviderToken(String clientRegistration, String accessToken) {
        super(clientRegistration, accessToken);
    }
}
