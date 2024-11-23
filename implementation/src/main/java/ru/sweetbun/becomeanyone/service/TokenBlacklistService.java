package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sweetbun.becomeanyone.entity.Token;
import ru.sweetbun.becomeanyone.repository.TokenRepository;

@RequiredArgsConstructor
@Service
public class TokenBlacklistService {

    private final TokenRepository tokenRepository;

    public void addTokenToBlacklist(String token, long expInMills) {
        Token tokenEntity = Token.builder()
                .id(token)
                .status("blacklisted")
                .expirationTime(System.currentTimeMillis() + expInMills)
                .build();
        tokenRepository.save(tokenEntity);
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenRepository.existsById(token);
    }
}