package com.example.FlightsCompare.repository;

import com.example.FlightsCompare.model.LinkedProvider;
import com.example.FlightsCompare.model.ProviderType;
import com.example.FlightsCompare.model.User;
import com.example.FlightsCompare.model.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserCreationRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LinkedProviderRepository linkedProviderRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("Test user creation without linked provider")
    public void testUserCreationWithoutLinkedProvider() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .roles(List.of(UserRole.MEMBER))
                .avatarUrl("someUrl")
                .username("test")
                .email("testing@gmail.com")
                .build();

        User savedUser = userRepository.saveAndFlush(user);
        entityManager.detach(savedUser);

        User fetchedUser = userRepository.findById(userId).orElse(null);
        assertEquals(user, fetchedUser, "User is not saved correctly");
    }

    @Test
    @DisplayName("Test user creation with linked provider")
    public void testUserCreationWithLinkedProvider() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .avatarUrl("someUrl")
                .username("test")
                .email("testing@gmail.com")
                .build();

        LinkedProvider linkedProvider = LinkedProvider.builder()
                .providerUserId("123")
                .provider(ProviderType.GITHUB)
                .userId(userId)
                .linkedAtDateInSeconds(Instant.now().getEpochSecond())
                .build();

        user.getLinkedProviders().add(linkedProvider);

        User savedUser = userRepository.saveAndFlush(user);
        entityManager.detach(savedUser);

        User fetchedUser = userRepository.findById(userId).orElse(null);
        assertEquals(user, fetchedUser, "User is not saved correctly");

        List<LinkedProvider> linkedProviders = linkedProviderRepository.findByUserId(userId);
        assertEquals(user.getLinkedProviders(), linkedProviders, "Linked provider is not saved correctly");
    }

    @Test
    @DisplayName("Test linked provider overwrite should result in merge in database not overwrite")
    public void testUserOverwritingLinkedProvider() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .avatarUrl("someUrl")
                .username("test")
                .email("testing@gmail.com")
                .build();

        List<LinkedProvider> linkedProviders = List.of(
                LinkedProvider.builder()
                        .providerUserId("123")
                        .provider(ProviderType.GITHUB)
                        .userId(userId)
                        .linkedAtDateInSeconds(Instant.now().getEpochSecond())
                        .build(),
                LinkedProvider.builder()
                        .providerUserId("123")
                        .provider(ProviderType.DISCORD)
                        .userId(userId)
                        .linkedAtDateInSeconds(Instant.now().getEpochSecond())
                        .build()
        );

        user.getLinkedProviders().add(linkedProviders.get(0));

        User savedUser = userRepository.saveAndFlush(user);
        entityManager.detach(savedUser);

        user.getLinkedProviders().set(0, linkedProviders.get(1));

        User savedUser2 = userRepository.saveAndFlush(user);
        entityManager.detach(savedUser2);

        User fetchedUser = userRepository.findById(userId).orElse(null);
        assertNotNull(fetchedUser, "User should not be null");

        assertEquals(linkedProviders, fetchedUser.getLinkedProviders(), "User is not saved correctly");
    }

    @Test
    @DisplayName("Test linked provider unique constraint violation of user_id + provider")
    public void testUserLinkedProviderUniqueConstraintViolation() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .avatarUrl("someUrl")
                .username("test")
                .email("testing@gmail.com")
                .linkedProviders(new ArrayList<>(List.of(
                        LinkedProvider.builder()
                                .providerUserId("123")
                                .provider(ProviderType.GITHUB)
                                .userId(userId)
                                .linkedAtDateInSeconds(Instant.now().getEpochSecond())
                                .build()
                )))
                .build();

        User savedUser = userRepository.saveAndFlush(user);

        LinkedProvider toClone = savedUser.getLinkedProviders().get(0);
        LinkedProvider clonedLinkedProvider = LinkedProvider.builder()
                .provider(toClone.getProvider())
                .providerUserId(toClone.getProviderUserId())
                .userId(toClone.getUserId())
                .linkedAtDateInSeconds(toClone.getLinkedAtDateInSeconds())
                .build();

        clonedLinkedProvider.setProviderUserId("modified");
        savedUser.getLinkedProviders().add(clonedLinkedProvider);

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(savedUser));
    }

    @Test
    @DisplayName("Test linked provider delete + insert providerUserId")
    public void testUserModifyingLinkedProvider() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .avatarUrl("someUrl")
                .username("test")
                .email("testing@gmail.com")
                .linkedProviders(new ArrayList<>(List.of(
                        LinkedProvider.builder()
                                .providerUserId("123")
                                .provider(ProviderType.GITHUB)
                                .userId(userId)
                                .linkedAtDateInSeconds(Instant.now().getEpochSecond())
                                .build()
                )))
                .build();

        User savedUser = userRepository.saveAndFlush(user);

        LinkedProvider toClone = savedUser.getLinkedProviders().get(0);
        LinkedProvider clonedLinkedProvider = LinkedProvider.builder()
                        .provider(toClone.getProvider())
                        .providerUserId(toClone.getProviderUserId())
                        .userId(toClone.getUserId())
                        .linkedAtDateInSeconds(toClone.getLinkedAtDateInSeconds())
                        .build();

        clonedLinkedProvider.setProviderUserId("modified");
        savedUser.getLinkedProviders().remove(0);
        linkedProviderRepository.delete(toClone);
        linkedProviderRepository.flush(); // without flush, the delete is executed after the insert and causes a unique constraint violation
        savedUser.getLinkedProviders().add(clonedLinkedProvider);

        User savedUser2 = userRepository.saveAndFlush(savedUser);
        entityManager.detach(savedUser);
        entityManager.detach(savedUser2);

        User fetchedUser = userRepository.findById(userId).orElse(null);
        assertNotNull(fetchedUser, "User should not be null");

        assertEquals(LinkedProvider.builder()
                .providerUserId("modified")
                .provider(ProviderType.GITHUB)
                .userId(userId)
                .linkedAtDateInSeconds(Instant.now().getEpochSecond())
                .build(), fetchedUser.getLinkedProviders().get(0), "User is not saved correctly");
        assertEquals(1, fetchedUser.getLinkedProviders().size(), "Too many linked providers");
    }

    @Test
    @DisplayName("Test change provider's user id")
    public void testChangeProviderId() {
        UUID userId = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .avatarUrl("someUrl")
                .username("test")
                .email("testing@gmail.com")
                .linkedProviders(new ArrayList<>(List.of(
                        LinkedProvider.builder()
                                .providerUserId("123")
                                .provider(ProviderType.GITHUB)
                                .userId(userId)
                                .linkedAtDateInSeconds(Instant.now().getEpochSecond())
                                .build()
                )))
                .build();

        User savedUser = userRepository.saveAndFlush(user);

        savedUser.getLinkedProviders().get(0).setUserId(userId2);
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(savedUser));

        User newUser = User.builder()
                .id(userId2)
                .avatarUrl("someUrl")
                .username("test2")
                .email("testing2@gmail.com")
                .build();
        User savedNewUser = userRepository.saveAndFlush(newUser);
        entityManager.detach(savedNewUser);

        userRepository.saveAndFlush(savedUser);
        entityManager.detach(savedUser);

        User fetchedUser = userRepository.findById(userId2).orElse(null);
        assertNotNull(fetchedUser, "User should not be null");
        assertEquals(1, fetchedUser.getLinkedProviders().size(), "Provider id not changed");
    }
}