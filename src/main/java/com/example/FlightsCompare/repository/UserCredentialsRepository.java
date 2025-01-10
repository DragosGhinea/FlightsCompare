package com.example.FlightsCompare.repository;

import com.example.FlightsCompare.model.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, UUID> {
}
