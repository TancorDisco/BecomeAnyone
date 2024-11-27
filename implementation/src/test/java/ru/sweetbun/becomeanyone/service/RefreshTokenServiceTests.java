package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.sweetbun.becomeanyone.entity.Token;
import ru.sweetbun.becomeanyone.repository.TokenRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTests {

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private final String username = "testUser";
    private final String refreshToken = "testToken";
    private final long expirationTime = 10000L;

    private Token activeToken;

    @BeforeEach
    void setUp() {
        activeToken = Token.builder().id(refreshToken).username(username)
                .status("active").expirationTime(System.currentTimeMillis() + expirationTime)
                .build();
    }

    @Test
    void saveRefreshToken_ValidInput_TokenSaved() {
        // Arrange
        when(tokenRepository.save(any(Token.class))).thenReturn(activeToken);

        // Act
        refreshTokenService.saveRefreshToken(username, refreshToken, expirationTime);

        // Assert
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    void isRefreshTokenValid_ValidToken_ReturnsTrue() {
        // Arrange
        when(tokenRepository.findById(refreshToken)).thenReturn(Optional.of(activeToken));

        // Act
        boolean result = refreshTokenService.isRefreshTokenValid(refreshToken);

        // Assert
        assertTrue(result);
        verify(tokenRepository, times(1)).findById(refreshToken);
    }

    @Test
    void isRefreshTokenValid_TokenExpired_ThrowsException() {
        // Arrange
        Token expiredToken = Token.builder().id(refreshToken).username(username)
                .status("active").expirationTime(System.currentTimeMillis() - 1000)
                .build();
        when(tokenRepository.findById(refreshToken)).thenReturn(Optional.of(expiredToken));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> refreshTokenService.isRefreshTokenValid(refreshToken));
        assertEquals("Token has not been found or its expiration date has expired", exception.getMessage());
        verify(tokenRepository, times(1)).findById(refreshToken);
    }

    @Test
    void deleteRefreshToken_ExistingToken_TokenDeleted() {
        // Arrange
        doNothing().when(tokenRepository).deleteById(refreshToken);

        // Act
        refreshTokenService.deleteRefreshToken(refreshToken);

        // Assert
        verify(tokenRepository, times(1)).deleteById(refreshToken);
    }

    @Test
    void deleteAllRefreshTokensForUser_ValidUser_TokensDeleted() {
        // Arrange
        Token token1 = Token.builder().id("token1").username(username).build();
        Token token2 = Token.builder().id("token2").username(username).build();
        when(tokenRepository.findAll()).thenReturn(List.of(token1, token2));

        // Act
        refreshTokenService.deleteAllRefreshTokensForUser(username);

        // Assert
        verify(tokenRepository, times(1)).deleteById("token1");
        verify(tokenRepository, times(1)).deleteById("token2");
    }

    @Test
    void getUsernameByRefreshToken_ValidToken_ReturnsUsername() {
        // Arrange
        when(tokenRepository.findById(refreshToken)).thenReturn(Optional.of(activeToken));

        // Act
        String result = refreshTokenService.getUsernameByRefreshToken(refreshToken);

        // Assert
        assertEquals(username, result);
        verify(tokenRepository, times(1)).findById(refreshToken);
    }

    @Test
    void getUsernameByRefreshToken_InvalidToken_ReturnsNull() {
        // Arrange
        when(tokenRepository.findById(refreshToken)).thenReturn(Optional.empty());

        // Act
        String result = refreshTokenService.getUsernameByRefreshToken(refreshToken);

        // Assert
        assertNull(result);
        verify(tokenRepository, times(1)).findById(refreshToken);
    }
}
