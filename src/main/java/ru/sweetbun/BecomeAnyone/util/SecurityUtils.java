package ru.sweetbun.BecomeAnyone.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.sweetbun.BecomeAnyone.entity.User;
import ru.sweetbun.BecomeAnyone.service.UserService;

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
