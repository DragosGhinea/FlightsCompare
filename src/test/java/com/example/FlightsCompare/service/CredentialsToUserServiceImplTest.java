package com.example.FlightsCompare.service;

import com.example.FlightsCompare.exception.*;
import com.example.FlightsCompare.model.*;
import com.example.FlightsCompare.repository.UserCredentialsRepository;
import com.example.FlightsCompare.repository.UserRepository;
import com.example.FlightsCompare.service.CredentialsToUserService;
import com.example.FlightsCompare.service.TokenService;
import com.example.FlightsCompare.service.impl.CredentialsToUserServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CredentialsToUserServiceImpl, focusing on register/login scenarios 
 * that parallel the "controller-level" scenarios you described earlier.
 */
// Generated with the help of ChatGPT
@ExtendWith(MockitoExtension.class)
class CredentialsToUserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCredentialsRepository userCredentialsRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    private CredentialsToUserService service;

    @BeforeEach
    void setUp() {
        // Create a fresh instance for each test.
        service = new CredentialsToUserServiceImpl(
                userRepository,
                userCredentialsRepository,
                passwordEncoder,
                tokenService
        );
    }

    // -----------------------------------------------------------------------------
    // SCENARIO 1:
    // "Firstly login the user via oauth2, then try logging in with credentials to see the triggered error."
    //
    // Implementation detail for service test:
    // We'll simulate that the user exists in DB with NO credentials but with a linked provider.
    // Then call service.login(...) => expect "UserNoCredentials" or "UserBadCredentials" error.
    // -----------------------------------------------------------------------------
    @Test
    @DisplayName("Scenario 1: A user with only OAuth2 (no credentials) tries to login => should fail")
    void scenario1_loginOAuth2UserWithNoCredentials() {
        // ARRANGE: Suppose the user has a linked provider but no credentials
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail("oauth2user@example.com");
        user.setLinkedProviders(List.of(new LinkedProvider())); // Not empty => indicates OAuth2
        user.setCredentials(null);  // No password-based credentials

        when(userRepository.findByEmail("oauth2user@example.com"))
                .thenReturn(Optional.of(user));

        // ACT + ASSERT
        // Attempt to login with email/password => expect a UserNoCredentials (or UserBadCredentials) 
        // based on your code. In your code, it's "if (user.getCredentials() == null) => throw new UserNoCredentials()".
        assertThrows(UserNoCredentials.class, () ->
                service.login("oauth2user@example.com", "somePassword")
        );
    }

    // -----------------------------------------------------------------------------
    // SCENARIO 2:
    // "Firstly login via oauth2 (simulate user in DB with linked provider, no credentials),
    //  then try registering the user WITHOUT an accessToken => expect error,
    //  then register WITH an accessToken => success, 
    //  then login with credentials => success,
    //  and also 'login with oauth2' again => (we just confirm the user is still present).
    //
    // In service-layer terms:
    //  - We'll set up the user in the repository (with a linked provider, no credentials)
    //  - Attempt register(..., null) => fails (UserAccessTokenRequired)
    //  - Attempt register(..., "validToken") => success -> user now has credentials
    //  - login(...) => success
    //  - re-register with same email => now should fail "User with this email already exists" 
    //    or if you want "oauth2 again," we can just skip or confirm the user is unchanged
    // -----------------------------------------------------------------------------
    @Test
    @DisplayName("Scenario 2: existing OAuth2 user => register no token => fail => register with token => success => login => success => re-register => fail")
    void scenario2_oauth2ThenRegisterFlow() {
        // 1) ARRANGE: user in DB with linked providers, no credentials
        UUID userId = UUID.randomUUID();
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("oauthuser@example.com");
        existingUser.setLinkedProviders(List.of(new LinkedProvider()));
        existingUser.setCredentials(null);

        when(userRepository.findByEmail("oauthuser@example.com"))
                .thenReturn(Optional.of(existingUser));

        // 2) Try register user with no access token => expect error
        assertThrows(UserAccessTokenRequired.class, () ->
                service.register("AnyName", "oauthuser@example.com", "somePass", null)
        );

        // 3) Now mock the token checks: assume valid
        //    In your code, you check tokenService.getRefreshToken(...) and isAccessTokenValid(...).
        when(tokenService.getRefreshToken(eq(userId), eq(false)))
                .thenReturn(new RefreshToken()); // dummy
        when(tokenService.isAccessTokenValid(any(), eq("validToken")))
                .thenReturn(true);

        // Also mock passwordEncoder
        when(passwordEncoder.encode("somePass")).thenReturn("encodedPass");

        // 4) register with access token => success
        User userAfterRegister = service.register("AnyName", "oauthuser@example.com", "somePass", "validToken");
        assertNotNull(userAfterRegister, "Should return the user");
        // At this point, the user is "existingUser" in your code, but with credentials set.
        // We can verify that credentials got saved:
        verify(userCredentialsRepository).save(any(UserCredentials.class));

        // 5) Now that user has credentials, attempt login => success
        //    Let's mock that userRepository still returns the same user (but with credentials).
        //    Typically, your code "userFound.setCredentials(...)" wouldn't persist automatically 
        //    without flush, but let's keep it simple. We'll just manually set it here or in the mock.
        UserCredentials userCred = new UserCredentials();
        userCred.setHashedPassword("encodedPass");
        existingUser.setCredentials(userCred);

        when(userRepository.findByEmail("oauthuser@example.com"))
                .thenReturn(Optional.of(existingUser));

        // Because we do "passwordEncoder.matches(...)"
        when(passwordEncoder.matches(eq("somePass"), eq("encodedPass"))).thenReturn(true);

        User loggedInUser = service.login("oauthuser@example.com", "somePass");
        assertEquals(existingUser, loggedInUser, "Login should return the same user object");

        // 6) Re-register the same user => "User with this email already exists"
        //    Because in your code, if the user has credentials => throw new UserBadCredentials("User with this email already exists");
        assertThrows(UserBadCredentials.class, () ->
                service.register("AnyName", "oauthuser@example.com", "somePass", "validToken")
        );
    }

    // -----------------------------------------------------------------------------
    // SCENARIO 3:
    // "Register the user first (has credentials), then 'login via oauth2' (simulate user 
    //  has a linked provider now?), then login with credentials again."
    //
    // We’ll interpret “login via oauth2” as: 
    //   the user has *some* linked provider or we do a second register call with an access token 
    //   while a user with credentials already exists. 
    //   Your code might not allow that scenario, but let's approximate.
    // -----------------------------------------------------------------------------
    @Test
    @DisplayName("Scenario 3: Register user with credentials => later attempt 'oauth2-style' register => then credentials login again")
    void scenario3_registerThenOAuth2ThenCredentials() {
        // 1) Register a brand new user => no user found => create a new one
        when(userRepository.findByEmail("combo@example.com"))
                .thenReturn(Optional.empty()); // user not found initially

        // For saving new user:
        doAnswer(invocation -> {
            User toSave = invocation.getArgument(0);
            toSave.setId(UUID.randomUUID()); // mock DB assigning ID
            return toSave;
        }).when(userRepository).save(any(User.class));

        // For saving new credentials:
        doAnswer(invocation -> {
            UserCredentials creds = invocation.getArgument(0);
            creds.setUserId(UUID.randomUUID()); // mock same or new ID 
            return creds;
        }).when(userCredentialsRepository).save(any(UserCredentials.class));

        when(passwordEncoder.encode("password123")).thenReturn("encoded123");

        User newUser = service.register("ComboUser", "combo@example.com", "password123");
        assertNotNull(newUser.getId(), "User should have an ID after registration");
        // Now user has credentials

        // 2) Simulate an "oauth2 flow" => call register(...) with an accessToken while 
        //    user is found + has credentials => your code should throw "User with this email already exists"
        when(userRepository.findByEmail("combo@example.com"))
                .thenReturn(Optional.of(newUser));  // now user is found

        assertThrows(UserBadCredentials.class, () ->
                service.register("ComboUser", "combo@example.com", "anotherPass", "oauth2token")
        );

        // 3) Attempt to login with credentials => success
        //    We must mock passwordEncoder.matches(...) now
        when(passwordEncoder.matches("password123", "encoded123")).thenReturn(true);

        User loggedIn = service.login("combo@example.com", "password123");
        assertEquals(newUser, loggedIn, "Should successfully log in with existing credentials");
    }

    // -----------------------------------------------------------------------------
    // SCENARIO 4:
    // "Login without registering => user not found"
    // -----------------------------------------------------------------------------
    @Test
    @DisplayName("Scenario 4: login() with an email not in DB => throw UserNotFound")
    void scenario4_loginNonExistentUser() {
        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFound.class, () ->
                service.login("unknown@example.com", "anyPass")
        );
    }

    // -----------------------------------------------------------------------------
    // SCENARIO 5:
    // "Try registering the same user twice => second time => error."
    // -----------------------------------------------------------------------------
    @Test
    @DisplayName("Scenario 5: Register the same user twice => second time => 'User with this email already exists'")
    void scenario5_registerSameUserTwice() {
        // ARRANGE 
        // 1) First call -> no user in DB -> user is created

        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        UserCredentials newCreds = new UserCredentials();
        newCreds.setUserId(newUser.getId());
        newUser.setCredentials(newCreds);

        when(userRepository.findByEmail("repeat@example.com"))
                .thenReturn(Optional.empty()) // first time
                .thenReturn(Optional.of(newUser)); // second time, user is found

        // We'll mimic saving the user
        doAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(UUID.randomUUID()); // emulate DB
            return u;
        }).when(userRepository).save(any(User.class));

        when(passwordEncoder.encode("mypassword")).thenReturn("encodedpass");

        // ACT
        // 1) Register first time -> success
        User first = service.register("RepeatUser", "repeat@example.com", "mypassword");
        assertNotNull(first, "Should create user on first register");

        // 2) Register second time -> user is found with credentials => throws
        assertThrows(UserBadCredentials.class, () ->
                service.register("RepeatUser", "repeat@example.com", "mypassword")
        );
    }

    @Test
    @DisplayName("Scenario 6: Login with incorrect password => should throw UserBadCredentials")
    void loginWrongPassword() {
        // ARRANGE
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail("some@example.com");

        UserCredentials creds = new UserCredentials();
        creds.setHashedPassword("hashedPass"); // The stored hash
        user.setCredentials(creds);

        when(userRepository.findByEmail("some@example.com"))
                .thenReturn(Optional.of(user));
        // Simulate wrong password by returning false
        when(passwordEncoder.matches("wrongPassword", "hashedPass")).thenReturn(false);

        // ACT + ASSERT
        // login(...) should throw UserBadCredentials if password doesn't match
        assertThrows(UserBadCredentials.class, () ->
                service.login("some@example.com", "wrongPassword")
        );
    }

}
