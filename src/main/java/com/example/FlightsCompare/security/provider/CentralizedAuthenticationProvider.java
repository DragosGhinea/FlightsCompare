package com.example.FlightsCompare.security.provider;

import com.example.FlightsCompare.service.CredentialsToUserService;
import com.example.FlightsCompare.service.impl.OAuth2ToUserServiceCentralized;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CentralizedAuthenticationProvider implements AuthenticationProvider {

    private final List<AuthenticationProvider> authenticationProviders = new ArrayList<>();

    public CentralizedAuthenticationProvider(OAuth2ToUserServiceCentralized oAuth2ToUserService, CredentialsToUserService credentialsToUserService) {
        LinkedProviderAuthenticationProvider linkedProviderAuthenticationProvider = new LinkedProviderAuthenticationProvider(oAuth2ToUserService);
        RegisterCredentialsAuthenticationProvider registerCredentialsAuthenticationProvider = new RegisterCredentialsAuthenticationProvider(credentialsToUserService);
        LoginCredentialsAuthenticationProvider loginCredentialsAuthenticationProvider = new LoginCredentialsAuthenticationProvider(credentialsToUserService);

        authenticationProviders.add(linkedProviderAuthenticationProvider);
        authenticationProviders.add(registerCredentialsAuthenticationProvider);
        authenticationProviders.add(loginCredentialsAuthenticationProvider);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        for (AuthenticationProvider authenticationProvider : authenticationProviders) {
            if (authenticationProvider.supports(authentication.getClass())) {
                return authenticationProvider.authenticate(authentication);
            }
        }

        // should never happen
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authenticationProviders.stream().anyMatch(provider -> provider.supports(authentication));
    }
}
