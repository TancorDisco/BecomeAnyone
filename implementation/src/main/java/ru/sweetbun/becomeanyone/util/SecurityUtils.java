package ru.sweetbun.becomeanyone.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.sweetbun.becomeanyone.entity.User;
import ru.sweetbun.becomeanyone.service.UserServiceImpl;

@RequiredArgsConstructor
@Component
public class SecurityUtils {
    @Lazy
    private final UserServiceImpl userServiceImpl;

    public String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public User getCurrentUser() {
        return userServiceImpl.getUserByUsername(getCurrentUsername());
    }
}
