package com.example.FlightsCompare.security.provider;

import com.example.FlightsCompare.model.User;
import com.example.FlightsCompare.security.tokens.RegisterToken;
import com.example.FlightsCompare.service.CredentialsToUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

//@Component
@RequiredArgsConstructor
public class RegisterCredentialsAuthenticationProvider implements AuthenticationProvider {

    private final CredentialsToUserService authenticationService;

    /**
     * Receive UsernamePasswordAuthenticationToken as RegisterToken
     * where username is the (username, email) pair
     * where password is the (password, access_token(optional)) pair
     */
    @Override
    @SuppressWarnings("unchecked")
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final Map.Entry<String, String> usernameAndEmail = (Map.Entry<String, String>) authentication.getPrincipal();
        final Map.Entry<String, String> passwordAndAccessToken = (Map.Entry<String, String>) authentication.getCredentials();

        User user = authenticationService.register(usernameAndEmail.getKey(), usernameAndEmail.getValue(), passwordAndAccessToken.getKey(), passwordAndAccessToken.getValue());

        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(RegisterToken.class);
    }
}