package ru.sweetbun.becomeanyone.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomUserPrincipal{
    private final Long id;
    private final String username;
}
