package com.example.FlightsCompare.service.impl;

import com.example.FlightsCompare.exception.UserAccessTokenRequired;
import com.example.FlightsCompare.exception.UserBadCredentials;
import com.example.FlightsCompare.exception.UserNoCredentials;
import com.example.FlightsCompare.exception.UserNotFound;
import com.example.FlightsCompare.model.*;
import com.example.FlightsCompare.repository.UserCredentialsRepository;
import com.example.FlightsCompare.repository.UserRepository;
import com.example.FlightsCompare.service.CredentialsToUserService;
import com.example.FlightsCompare.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CredentialsToUserServiceImpl implements CredentialsToUserService {
    private final UserRepository userRepository;
    private final UserCredentialsRepository userCredentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFound::new);

        if (user.getCredentials() == null)
            throw new UserNoCredentials();

        UserCredentials credentials = user.getCredentials();
        if (!passwordEncoder.matches(password, credentials.getHashedPassword()))
            throw new UserBadCredentials("Invalid password");

        return user;
    }

    @Override
    public User register(String username, String email, String password) {
        return register(username, email, password, null);
    }

    @Override
    @Transactional
    public User register(String username, String email, String password, String accessToken) {
        Optional<User> user = userRepository.findByEmail(email);
        // if user was registered via linked providers and doesn't have credentials
        // we need to use the access token as confirmation of the user's identity
        // before registering them with credentials
        if (user.isPresent()) {
            User userFound = user.get();
            if (userFound.getCredentials() != null)
                throw new UserBadCredentials("User with this email already exists");

            if (!userFound.getLinkedProviders().isEmpty()) {
                if (accessToken == null)
                    throw new UserAccessTokenRequired();

                RefreshToken token = tokenService.getRefreshToken(userFound.getId(), false);
                if (!tokenService.isAccessTokenValid(token, accessToken))
                    throw new UserBadCredentials("Invalid access token");

                UserCredentials credentials = new UserCredentials();
                credentials.setHashedPassword(passwordEncoder.encode(password));
                credentials.setUserId(userFound.getId());

                userCredentialsRepository.save(credentials);

                return userFound;
            }
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(username);
        newUser.setId(UUID.randomUUID());
        newUser.setRoles(List.of(UserRole.MEMBER));

        userRepository.save(newUser);

        UserCredentials credentials = new UserCredentials();
        credentials.setUserId(newUser.getId());
        credentials.setHashedPassword(passwordEncoder.encode(password));

        userCredentialsRepository.save(credentials);

        newUser.setCredentials(credentials);

        return newUser;
    }
}
