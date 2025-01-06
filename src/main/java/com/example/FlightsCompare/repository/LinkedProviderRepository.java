package com.example.FlightsCompare.repository;

import com.example.FlightsCompare.model.LinkedProvider;
import com.example.FlightsCompare.model.LinkedProviderId;
import com.example.FlightsCompare.model.ProviderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LinkedProviderRepository extends JpaRepository<LinkedProvider, LinkedProviderId> {

    @Query("SELECT lp FROM LinkedProvider lp JOIN lp.user u WHERE u.email = :email AND lp.provider = :provider")
    LinkedProvider findByEmailAndProvider(String email, ProviderType provider);

    List<LinkedProvider> findByUserId(UUID userId);

    LinkedProvider findByProviderUserIdAndProvider(String providerUserId, ProviderType provider);

}