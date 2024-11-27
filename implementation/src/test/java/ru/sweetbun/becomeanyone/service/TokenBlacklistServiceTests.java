package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.sweetbun.becomeanyone.entity.Token;
import ru.sweetbun.becomeanyone.repository.TokenRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTests {

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    private String token;

    @BeforeEach
    void setUp() {
        token = "test-token";
    }

    @Test
    void addTokenToBlacklist_ValidInput_TokenSavedSuccessfully() {
        long expInMills = 3600000L;

        tokenBlacklistService.addTokenToBlacklist(token, expInMills);

        //Assert
        ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
        verify(tokenRepository, times(1)).save(tokenCaptor.capture());

        Token savedToken = tokenCaptor.getValue();
        assertEquals(token, savedToken.getId());
        assertEquals("blacklisted", savedToken.getStatus());
        assertTrue(savedToken.getExpirationTime() > System.currentTimeMillis());
    }

    @Test
    void isTokenBlacklisted_TokenExists_ReturnsTrue() {
        when(tokenRepository.existsById(token)).thenReturn(true);

        boolean isBlacklisted = tokenBlacklistService.isTokenBlacklisted(token);

        assertTrue(isBlacklisted);
        verify(tokenRepository, times(1)).existsById(token);
    }

    @Test
    void isTokenBlacklisted_TokenDoesNotExist_ReturnsFalse() {
        when(tokenRepository.existsById(token)).thenReturn(false);

        boolean isBlacklisted = tokenBlacklistService.isTokenBlacklisted(token);

        assertFalse(isBlacklisted);
        verify(tokenRepository, times(1)).existsById(token);
    }
}
