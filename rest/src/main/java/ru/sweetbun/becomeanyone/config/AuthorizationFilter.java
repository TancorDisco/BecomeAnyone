package ru.sweetbun.becomeanyone.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.sweetbun.becomeanyone.service.TokenBlacklistService;
import ru.sweetbun.becomeanyone.service.TokenService;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@Profile("!test")
@RequiredArgsConstructor
@Slf4j
@Component
public class AuthorizationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/auth/login") || path.startsWith("/auth/register") || path.startsWith("/swagger")
                || path.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String requestPath = request.getRequestURI();
        log.info("Processing request to {}", requestPath);

        if (header == null || header.isBlank()) {
            String message = "Authorization header is missing or blank.";
            log.warn(message);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new IllegalStateException(message);
        }
        if (!checkAuthorization(header)) {
            String message = "Authorization header is invalid or token is not valid.";
            log.warn(message);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            throw new AccessDeniedException(message);
        }
        String token = header.substring(7);
        if (tokenBlacklistService.isTokenBlacklisted(token)) {
            String message = "Token is blacklisted.";
            log.warn(message);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new IllegalStateException(message);
        }

        String username = tokenService.getUsernameFromToken(token);
        Long userId = tokenService.getUserIdFromToken(token);
        List<GrantedAuthority> authorities = tokenService.getAuthoritiesFromToken(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    new CustomUserPrincipal(userId, username),
                    null,
                    authorities
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private boolean checkAuthorization(String auth) {
        if (!auth.startsWith("Bearer ")) return false;
        return tokenService.validateToken(auth.substring(7));
    }
}
