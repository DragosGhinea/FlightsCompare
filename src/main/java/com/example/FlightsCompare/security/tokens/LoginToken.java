package com.example.FlightsCompare.security.tokens;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
@Setter
public class LoginToken extends UsernamePasswordAuthenticationToken {

    public LoginToken(String email, String password) {
        super(email, password);
    }
}
