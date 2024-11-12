package ru.sweetbun.become_anyone.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.sweetbun.become_anyone.entity.User;
import ru.sweetbun.become_anyone.service.UserService;

@RequiredArgsConstructor
@Component
public class SecurityUtils {
    @Lazy
    private final UserService userService;

    public String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public User getCurrentUser() {
        return userService.getUserByUsername(getCurrentUsername());
    }
}
