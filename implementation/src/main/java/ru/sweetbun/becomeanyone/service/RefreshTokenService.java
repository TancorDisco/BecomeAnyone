package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.entity.Token;
import ru.sweetbun.becomeanyone.repository.TokenRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final TokenRepository tokenRepository;

    @Transactional
    public void saveRefreshToken(String username, String refreshToken, long expirationTime) {
        Token tokenEntity = Token.builder()
                .id(refreshToken)
                .username(username)
                .status("active")
                .expirationTime(System.currentTimeMillis() + expirationTime)
                .build();
        tokenRepository.save(tokenEntity);

    }

    public boolean isRefreshTokenValid(String refreshToken) {
        Token token = tokenRepository.findById(refreshToken).orElse(null);
        if (token == null || System.currentTimeMillis() > token.getExpirationTime()) {
            throw new IllegalArgumentException("Token has not been found or its expiration date has expired");
        }
        return true;
    }

    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        tokenRepository.deleteById(refreshToken);
    }

    @Transactional
    public void deleteAllRefreshTokensForUser(String username) {
        tokenRepository.findAll().forEach(token -> {
            String name = token.getUsername();
            if (name != null && name.equals(username)) {
                deleteRefreshToken(token.getId());
            }
        });
    }

    public String getUsernameByRefreshToken(String refreshToken) {
        Token token = tokenRepository.findById(refreshToken).orElse(null);
        return token != null ? token.getUsername() : null;
    }
}
