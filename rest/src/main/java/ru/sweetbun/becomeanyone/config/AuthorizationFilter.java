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
import ru.sweetbun.becomeanyone.model.CustomUserPrincipal;
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
        try {
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            String requestPath = request.getRequestURI();
            log.info("Processing request to {}", requestPath);

            if (header == null || header.isBlank()) {
                throw new IllegalStateException("Authorization header is missing or blank.");
            }
            if (!checkAuthorization(header)) {
                throw new AccessDeniedException("Authorization header is invalid or token is not valid.");
            }
            String token = header.substring(7);
            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                throw new IllegalStateException("Token is blacklisted.");
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
        } catch (IllegalStateException | AccessDeniedException ex) {
            log.error("Error during authorization: {}", ex.getMessage());
            response.setStatus(ex instanceof AccessDeniedException ? HttpServletResponse.SC_FORBIDDEN : HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(ex.getMessage());
        }
    }

    private boolean checkAuthorization(String auth) {
        if (!auth.startsWith("Bearer ")) return false;
        return tokenService.validateToken(auth.substring(7));
    }
}
