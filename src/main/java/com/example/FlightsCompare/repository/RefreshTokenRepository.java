package com.example.FlightsCompare.repository;

import com.example.FlightsCompare.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByUserId(UUID userId);

    @Query("SELECT rt FROM RefreshToken rt JOIN FETCH rt.user WHERE rt.userId = :userId")
    Optional<RefreshToken> findByUserIdWithUser(UUID userId);

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    @Query("SELECT rt FROM RefreshToken rt JOIN FETCH rt.user WHERE rt.refreshToken = :refreshToken")
    Optional<RefreshToken> findByRefreshTokenWithUser(String refreshToken);

    Integer deleteByUserId(UUID userId);

    Integer deleteByRefreshToken(String refreshToken);
}