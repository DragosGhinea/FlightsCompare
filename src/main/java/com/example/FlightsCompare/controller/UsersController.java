package com.example.FlightsCompare.controller;

import com.example.FlightsCompare.model.User;
import com.example.FlightsCompare.repository.LinkedProviderRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
public class UsersController {

    LinkedProviderRepository linkedProviderRepository;

    @GetMapping("/me")
    public ResponseEntity<User> getUserMe(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Must be logged in to access this endpoint");
        }

        if (!(authentication.getPrincipal() instanceof User user)) {
            System.out.println("Authentication is not a User " + authentication.getPrincipal());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        user.getLinkedProviders(); // lazy load

        return ResponseEntity.ok(user);
    }
}