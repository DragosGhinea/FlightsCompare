package com.example.FlightsCompare.security.tokens;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.AbstractMap;
import java.util.Map;

@Getter
@Setter
public class RegisterToken extends UsernamePasswordAuthenticationToken {

    public RegisterToken(String username, String email, String password, String accessToken) {
        super(new AbstractMap.SimpleEntry<>(username, email), new AbstractMap.SimpleEntry<>(password, accessToken));
    }
}
