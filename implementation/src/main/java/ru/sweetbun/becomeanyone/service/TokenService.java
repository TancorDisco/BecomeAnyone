package ru.sweetbun.becomeanyone.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final long ACCESS_TOKEN_VALIDITY = 3_600_000; // 1 час
    private final long REFRESH_TOKEN_VALIDITY = 604_800_000; // 7 дней
    private final long REFRESH_TOKEN_REMEMBER_ME_VALIDITY = 2_592_000_000L; // 30 дней

    public String generateAccessToken(String username, Long userId, List<String> roles) {
        log.info("Generating token for user: {} with roles: {}", username, roles);
        return JWT.create()
                .withSubject(username)
                .withAudience("become-anyone")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                .withClaim("roles", roles)
                .withClaim("id", userId)
                .sign(getAlgorithm());
    }

    public String generateRefreshToken(String username, boolean rememberMe) {
        long validity = (rememberMe) ? REFRESH_TOKEN_REMEMBER_ME_VALIDITY : REFRESH_TOKEN_VALIDITY;
        return JWT.create()
                .withSubject(username)
                .withAudience("become-anyone")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + validity))
                .sign(getAlgorithm());
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC512(jwtSecret);
    }

    private JWTVerifier getVerifier() {
        return JWT.require(getAlgorithm()).build();
    }

    public boolean validateToken(String token) {
        try {
            DecodedJWT jwt = getVerifier().verify(token);

            List<String> audience = jwt.getAudience();
            if (!audience.contains("become-anyone")) {
                log.error("Token audience is invalid: " + audience);
                return false;
            }
        } catch (JWTVerificationException e) {
            log.error("Token is invalid: " + e.getMessage());
            return false;
        }
        return true;
    }

    public String getUsernameFromToken(String token) {
        DecodedJWT decodedJWT = getVerifier().verify(token);
        return decodedJWT.getSubject();
    }

    public long getExpirationTimeInMills(String token) {
        DecodedJWT decodedJWT = getVerifier().verify(token);
        Date exporationDate = decodedJWT.getExpiresAt();
        return exporationDate.getTime() - new Date().getTime();
    }

    public List<GrantedAuthority> getAuthoritiesFromToken(String token) {
        DecodedJWT decodedJWT = getVerifier().verify(token);
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public Long getUserIdFromToken(String token) {
        DecodedJWT decodedJWT = getVerifier().verify(token);
        return decodedJWT.getClaim("id").asLong();
    }
}
