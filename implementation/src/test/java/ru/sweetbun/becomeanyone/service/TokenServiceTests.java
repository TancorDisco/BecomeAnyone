package ru.sweetbun.becomeanyone.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTests {

    @InjectMocks
    private TokenService tokenService;

    private static final String JWT_SECRET = "testSecret";
    private static final long ACCESS_TOKEN_VALIDITY = 600_000; // 10 minutes
    private static final long REFRESH_TOKEN_VALIDITY = 2_592_000_000L; // 30 days
    private static final long REFRESH_TOKEN_REMEMBER_ME_VALIDITY = 5_184_000_000L; // 60 days

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenService, "jwtSecret", JWT_SECRET);
        ReflectionTestUtils.setField(tokenService, "ACCESS_TOKEN_VALIDITY", ACCESS_TOKEN_VALIDITY);
        ReflectionTestUtils.setField(tokenService, "REFRESH_TOKEN_VALIDITY", REFRESH_TOKEN_VALIDITY);
        ReflectionTestUtils.setField(tokenService, "REFRESH_TOKEN_REMEMBER_ME_VALIDITY", REFRESH_TOKEN_REMEMBER_ME_VALIDITY);
    }

    @Test
    void generateAccessToken_ValidInputs_TokenContainsExpectedClaims() {
        // Arrange
        String username = "testUser";
        Long userId = 1L;
        List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN");

        // Act
        String token = tokenService.generateAccessToken(username, userId, roles);

        // Assert
        assertNotNull(token);
        DecodedJWT decodedJWT = JWT.decode(token);

        assertEquals(username, decodedJWT.getSubject());
        assertEquals("become-anyone", decodedJWT.getAudience().get(0));
        assertNotNull(decodedJWT.getIssuedAt());
        assertNotNull(decodedJWT.getExpiresAt());
        assertTrue(decodedJWT.getExpiresAt().getTime() > decodedJWT.getIssuedAt().getTime());

        List<String> tokenRoles = decodedJWT.getClaim("roles").asList(String.class);
        assertEquals(roles, tokenRoles);

        assertEquals(userId, decodedJWT.getClaim("id").asLong());
    }

    @Test
    void generateRefreshToken_RememberMeTrue_TokenValidityIsExtended() {
        // Arrange
        String username = "testUser";
        boolean rememberMe = true;

        // Act
        String token = tokenService.generateRefreshToken(username, rememberMe);

        // Assert
        assertNotNull(token);
        DecodedJWT decodedJWT = JWT.decode(token);

        assertEquals(username, decodedJWT.getSubject());
        assertEquals("become-anyone", decodedJWT.getAudience().get(0));

        long validityInMills = decodedJWT.getExpiresAt().getTime() - decodedJWT.getIssuedAt().getTime();
        assertEquals(REFRESH_TOKEN_REMEMBER_ME_VALIDITY, validityInMills);
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        // Arrange
        String token = JWT.create()
                .withAudience("become-anyone")
                .sign(Algorithm.HMAC512(JWT_SECRET));

        // Act
        boolean isValid = tokenService.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_InvalidAudience_ReturnsFalse() {
        // Arrange
        String token = JWT.create()
                .withAudience("wrong-audience")
                .sign(Algorithm.HMAC512(JWT_SECRET));

        // Act
        boolean isValid = tokenService.validateToken(token);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_ExpiredToken_ReturnsFalse() {
        // Arrange
        String token = JWT.create()
                .withAudience("become-anyone")
                .withExpiresAt(new Date(System.currentTimeMillis() - 1))
                .sign(Algorithm.HMAC512(JWT_SECRET));

        // Act
        boolean isValid = tokenService.validateToken(token);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void getUsernameFromToken_ValidToken_ReturnsCorrectUsername() {
        // Arrange
        String username = "testUser";
        String token = JWT.create()
                .withSubject(username)
                .sign(Algorithm.HMAC512(JWT_SECRET));

        // Act
        String extractedUsername = tokenService.getUsernameFromToken(token);

        // Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    void getUserIdFromToken_ValidToken_ReturnsCorrectUserId() {
        // Arrange
        Long userId = 123L;
        String token = JWT.create()
                .withClaim("id", userId)
                .sign(Algorithm.HMAC512(JWT_SECRET));

        // Act
        Long extractedUserId = tokenService.getUserIdFromToken(token);

        // Assert
        assertEquals(userId, extractedUserId);
    }

    @Test
    void getAuthoritiesFromToken_ValidToken_ReturnsCorrectAuthorities() {
        // Arrange
        List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN");
        String token = JWT.create()
                .withClaim("roles", roles)
                .sign(Algorithm.HMAC512(JWT_SECRET));

        // Act
        List<?> authorities = tokenService.getAuthoritiesFromToken(token);

        // Assert
        assertNotNull(authorities);
        assertEquals(roles.size(), authorities.size());
        assertTrue(List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        ).containsAll(authorities));
    }

    @Test
    void getExpirationTimeInMills_ValidToken_ReturnsPositiveTime() {
        // Arrange
        long validity = 10_000; // 10 seconds
        String token = JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + validity))
                .sign(Algorithm.HMAC512(JWT_SECRET));

        // Act
        long remainingTime = tokenService.getExpirationTimeInMills(token);

        // Assert
        assertTrue(remainingTime > 0);
        assertTrue(remainingTime <= validity);
    }

    @Test
    void getExpirationTimeInMills_ExpiredToken_ThrowsException() {
        // Arrange
        String token = JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() - 10_000))
                .sign(Algorithm.HMAC512(JWT_SECRET));

        // Act & Assert
        assertThrows(JWTVerificationException.class, () -> tokenService.getExpirationTimeInMills(token));
    }
}