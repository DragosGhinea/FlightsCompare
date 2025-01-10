package com.example.FlightsCompare.security.provider;

import com.example.FlightsCompare.model.User;
import com.example.FlightsCompare.security.tokens.LoginToken;
import com.example.FlightsCompare.service.CredentialsToUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

//@Component
@RequiredArgsConstructor
public class LoginCredentialsAuthenticationProvider implements AuthenticationProvider {

    private final CredentialsToUserService authenticationService;

    /**
     * Receive UsernamePasswordAuthenticationToken as LoginToken
     * where username is the email
     * where password is the actual password
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String email = authentication.getName();
        final String password = authentication.getCredentials().toString();

        User user = authenticationService.login(email, password);

        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(LoginToken.class);
    }
}