package ru.sweetbun.becomeanyone.service;

import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

    public boolean isTokenBlacklisted(String token) {
        return false;
    }
}
