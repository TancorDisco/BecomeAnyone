package ru.sweetbun.becomeanyone.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.sweetbun.becomeanyone.model.CustomUserPrincipal;
import ru.sweetbun.becomeanyone.entity.User;
import ru.sweetbun.becomeanyone.service.UserServiceImpl;

@RequiredArgsConstructor
@Component
public class SecurityUtils {
    @Lazy
    private final UserServiceImpl userServiceImpl;

    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !((auth.getPrincipal()) instanceof CustomUserPrincipal)) {
            throw new IllegalStateException("No authentication user found");
        }
        CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();
        return principal.getUsername();
    }

    public User getCurrentUser() {
        return userServiceImpl.getUserByUsername(getCurrentUsername());
    }
}
